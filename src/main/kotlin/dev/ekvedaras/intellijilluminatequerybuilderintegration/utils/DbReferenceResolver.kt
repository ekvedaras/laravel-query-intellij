package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.columnsInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.schemasInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.tablesInParallel
import java.util.*

class DbReferenceResolver(private val reference: DbReferenceExpression) {
    fun resolve() {
        val schemas = Collections.synchronizedList(reference.schema)
        val tables = Collections.synchronizedList(reference.table)
        val columns = Collections.synchronizedList(reference.column)

        when (reference.type) {
            DbReferenceExpression.Companion.Type.Table ->
                ResolverForTableMethods(reference, schemas, tables).resolve()
            DbReferenceExpression.Companion.Type.Column ->
                ResolverForColumnMethods(reference, schemas, tables, columns).resolve()
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
        reference.project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel()
                .filter { it.name == reference.parts.first() }
                .forEach { schemas.add(it) }
        }
    }

    /**
     * 'table'
     */
    private fun resolveTables() {
        reference.project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.tablesInParallel().forEach { table ->
                if (table.name == reference.parts.last()) {
                    tables.add(table)
                } else if (reference.tablesAndAliases[reference.parts.last()]?.first == table.name) {
                    tables.add(table)
                    reference.alias = table.name
                }
            }
        }
    }

    /**
     * 'schema.table'
     */
    private fun resolveSchemaTables() {
        reference.project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel()
                .filter { schemas.contains(it) }
                .forEach { schema ->
                    dataSource.tablesInParallel()
                        .filter { it.dasParent?.name == schema.name }
                        .filter { it.name == reference.parts.last() }
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
        reference.project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel()
                .filter { it.name == reference.parts.first() }
                .forEach { schemas.add(it) }

            dataSource.tablesInParallel().forEach { dasTable ->
                if (dasTable.name == reference.parts.first()) {
                    tables.add(dasTable)
                } else if (reference.tablesAndAliases[reference.parts.first()]?.first == dasTable.name) {
                    tables.add(dasTable)
                }

                dasTable.columnsInParallel()
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
        reference.project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel()
                .filter { it.name == reference.parts.first() }
                .forEach { schemas.add(it) }

            dataSource.tablesInParallel().forEach { table ->
                if (schemas.isEmpty() || schemas.contains(table.dasParent)) {
                    if (table.name == reference.parts.first() || table.name == reference.parts.last()) {
                        tables.add(table)

                        table.columnsInParallel()
                            .filter { it.name == reference.parts.last() }
                            .forEach { columns.add(it) }
                    } else if (schemas.isEmpty() && (reference.tablesAndAliases[reference.parts.first()]?.first == table.name || reference.tablesAndAliases[reference.parts.last()]?.first == table.name)) {
                        tables.add(table)

                        table.columnsInParallel()
                            .filter { it.name == reference.parts.last() }
                            .forEach { columns.add(it) }
                    }
                }
            }
        }
    }

    /**
     * schema.table.column
     */
    private fun withThreeParts() {
        reference.project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel()
                .filter { it.name == reference.parts.first() }
                .forEach { schemas.add(it) }

            dataSource.tablesInParallel()
                .filter { schemas.contains(it.dasParent) }
                .forEach { table ->
                    if (table.name == reference.parts[1]) {
                        tables.add(table)

                        table.columnsInParallel()
                            .filter { it.name == reference.parts.last() }
                            .forEach { columns.add(it) }
                    } else if (reference.tablesAndAliases[reference.parts[1]]?.first == table.name) {
                        tables.add(table)

                        table.columnsInParallel()
                            .filter { it.name == reference.parts.last() }
                            .forEach { columns.add(it) }
                    }
                }
        }
    }
}
