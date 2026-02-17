package dev.ekvedaras.laravelquery.utils

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasForeignKey
import com.intellij.database.model.DasIndex
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.model.DasTableKey
import com.intellij.openapi.progress.ProgressManager
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.columns
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.dbDataSources
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.foreignKeys
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.indexes
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.keys
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.nameWithoutPrefix
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.schemas
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.tablesSequential
import java.util.Collections

class DbReferenceResolver(private val reference: DbReferenceExpression) {
    fun resolve() {
        val schemas = Collections.synchronizedList(reference.schema)
        val tables = Collections.synchronizedList(reference.table)
        val columns = Collections.synchronizedList(reference.column)
        val indexes = Collections.synchronizedList(reference.index)
        val keys = Collections.synchronizedList(reference.key)
        val foreignKeys = Collections.synchronizedList(reference.foreignKey)

        when (reference.type) {
            DbReferenceExpression.Companion.Type.Table ->
                ResolverForTableMethods(reference, schemas, tables).resolve()
            DbReferenceExpression.Companion.Type.Column ->
                ResolverForColumnMethods(reference, schemas, tables, columns).resolve()
            DbReferenceExpression.Companion.Type.Index ->
                ResolverForIndexMethods(reference, indexes).resolve()
            DbReferenceExpression.Companion.Type.Key ->
                ResolverForKeyMethods(reference, keys).resolve()
            DbReferenceExpression.Companion.Type.ForeignKey ->
                ResolverForForeignKeyMethods(reference, foreignKeys).resolve()
        }
    }
}

private class ResolverForTableMethods(
    private val reference: DbReferenceExpression,
    private val schemas: MutableList<DasNamespace>,
    private val tables: MutableList<DasTable>
) {
    fun resolve() {
        resolveSchemes()

        ProgressManager.checkCanceled()

        when (reference.parts.size) {
            1 -> resolveTables()
            else -> resolveSchemaTables()
        }
    }

    /**
     * 'schema'
     * 'schema.table'
     */
    private fun resolveSchemes() {
        reference.project.dbDataSources().forEach { dataSource ->
            dataSource.schemas()
                .filter { it.name == reference.parts.first() }
                .forEach { schemas.add(it) }
        }
    }

    /**
     * 'table'
     */
    private fun resolveTables() {
        reference.project.dbDataSources().forEach { dataSource ->
            ProgressManager.checkCanceled()

            dataSource.tablesSequential().forEach { table ->
                ProgressManager.checkCanceled()

                if (table.nameWithoutPrefix(reference.project) == reference.parts.last()) {
                    tables.add(table)
                } else if (reference.tablesAndAliases[reference.parts.last()]?.first == table.nameWithoutPrefix(reference.project)) {
                    tables.add(table)
                    reference.alias = table.nameWithoutPrefix(reference.project)
                }
            }
        }
    }

    /**
     * 'schema.table'
     */
    private fun resolveSchemaTables() {
        reference.project.dbDataSources().forEach { dataSource ->
            ProgressManager.checkCanceled()

            dataSource.schemas()
                .filter { schemas.contains(it) }
                .forEach { schema ->
                    ProgressManager.checkCanceled()

                    dataSource.tablesSequential()
                        .filter { it.dasParent?.name == schema.name }
                        .filter { it.nameWithoutPrefix(reference.project) == reference.parts.last() }
                        .forEach { tables.add(it) }
                }
        }
    }
}

private class ResolverForColumnMethods(
    private val reference: DbReferenceExpression,
    private val schemas: MutableList<DasNamespace>,
    private val tables: MutableList<DasTable>,
    private val columns: MutableList<DasColumn>
) {
    fun resolve() {
        when (reference.parts.size) {
            1 -> withOnePart()
            2 -> withTwoParts()
            else -> withThreeParts()
        }
    }

    /**
     * 'column'
     * 'table'
     * 'schema'
     * 'alias'
     */
    private fun withOnePart() {
        reference.project.dbDataSources().forEach { dataSource ->
            ProgressManager.checkCanceled()

            dataSource.schemas()
                .filter { it.name == reference.parts.first() }
                .forEach { schemas.add(it) }

            dataSource.tablesSequential().forEach { dasTable ->
                ProgressManager.checkCanceled()

                if (dasTable.nameWithoutPrefix(reference.project) == reference.parts.first()) {
                    tables.add(dasTable)
                } else if (reference.tablesAndAliases[reference.parts.first()]?.first == dasTable.nameWithoutPrefix(reference.project)) {
                    tables.add(dasTable)
                }

                dasTable.columns()
                    .filter { it.name == reference.parts.first() }
                    .forEach { columns.add(it) }
            }
        }
    }

    /**
     * 'table.column'
     * 'schema.table'
     * 'alias.column'
     */
    private fun withTwoParts() {
        reference.project.dbDataSources().forEach { dataSource ->
            ProgressManager.checkCanceled()

            dataSource.schemas()
                .filter { it.name == reference.parts.first() }
                .forEach { schemas.add(it) }

            dataSource.tablesSequential().forEach { table ->
                ProgressManager.checkCanceled()

                if (schemas.isEmpty() || schemas.contains(table.dasParent)) {
                    addTablesAndTheirColumns(table)
                }
            }
        }
    }

    private fun addTablesAndTheirColumns(table: DasTable) {
        if (table.nameWithoutPrefix(reference.project) == reference.parts.first() || table.nameWithoutPrefix(reference.project) == reference.parts.last()) {
            tables.add(table)

            table.columns()
                .filter { it.name == reference.parts.last() }
                .forEach { columns.add(it) }
        } else if (schemas.isEmpty() &&
            (
                reference.tablesAndAliases[reference.parts.first()]?.first == table.nameWithoutPrefix(reference.project) ||
                    reference.tablesAndAliases[reference.parts.last()]?.first == table.nameWithoutPrefix(reference.project)
                )
        ) {
            tables.add(table)

            table.columns()
                .filter { it.name == reference.parts.last() }
                .forEach { columns.add(it) }
        }
    }

    /**
     * schema.table.column
     */
    private fun withThreeParts() {
        reference.project.dbDataSources().forEach { dataSource ->
            ProgressManager.checkCanceled()

            dataSource.schemas()
                .filter { it.name == reference.parts.first() }
                .forEach { schemas.add(it) }

            ProgressManager.checkCanceled()

            dataSource.tablesSequential()
                .filter { schemas.contains(it.dasParent) }
                .forEach { addTableAndItsColumns(it) }
        }
    }

    private fun addTableAndItsColumns(table: DasTable) {
        if (table.nameWithoutPrefix(reference.project) == reference.parts[1]) {
            tables.add(table)

            table.columns()
                .filter { it.name == reference.parts.last() }
                .forEach { columns.add(it) }
        } else if (reference.tablesAndAliases[reference.parts[1]]?.first == table.nameWithoutPrefix(reference.project)) {
            tables.add(table)

            table.columns()
                .filter { it.name == reference.parts.last() }
                .forEach { columns.add(it) }
        }
    }
}

private class ResolverForIndexMethods(
    private val reference: DbReferenceExpression,
    private val indexes: MutableList<DasIndex>,
) {
    fun resolve() {
        reference.project.dbDataSources().forEach { dataSource ->
            ProgressManager.checkCanceled()

            dataSource.tablesSequential().filter {
                reference.tablesAndAliases.containsKey(it.nameWithoutPrefix(reference.project))
            }.filter {
                (reference.tablesAndAliases[it.nameWithoutPrefix(reference.project)]?.second ?: it.dasParent?.name) == it.dasParent?.name
            }.forEach { table ->
                ProgressManager.checkCanceled()

                table.indexes()
                    .filter { it.name == reference.parts[0] }
                    .forEach { indexes.add(it) }
            }
        }
    }
}

private class ResolverForKeyMethods(
    private val reference: DbReferenceExpression,
    private val keys: MutableList<DasTableKey>,
) {
    fun resolve() {
        reference.project.dbDataSources().forEach { dataSource ->
            ProgressManager.checkCanceled()

            dataSource.tablesSequential().filter {
                reference.tablesAndAliases.containsKey(it.nameWithoutPrefix(reference.project))
            }.filter {
                (reference.tablesAndAliases[it.nameWithoutPrefix(reference.project)]?.second ?: it.dasParent?.name) == it.dasParent?.name
            }.forEach { table ->
                ProgressManager.checkCanceled()

                table.keys()
                    .filter { it.name == reference.parts[0] }
                    .forEach { keys.add(it) }
            }
        }
    }
}

private class ResolverForForeignKeyMethods(
    private val reference: DbReferenceExpression,
    private val foreignKeys: MutableList<DasForeignKey>,
) {
    fun resolve() {
        reference.project.dbDataSources().forEach { dataSource ->
            ProgressManager.checkCanceled()

            dataSource.tablesSequential().filter {
                reference.tablesAndAliases.containsKey(it.nameWithoutPrefix(reference.project))
            }.filter {
                (reference.tablesAndAliases[it.nameWithoutPrefix(reference.project)]?.second ?: it.dasParent?.name) == it.dasParent?.name
            }.forEach { table ->
                ProgressManager.checkCanceled()

                table.foreignKeys()
                    .filter { it.name == reference.parts[0] }
                    .forEach { foreignKeys.add(it) }
            }
        }
    }
}
