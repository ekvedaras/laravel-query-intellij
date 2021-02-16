package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import dev.ekvedaras.intellijilluminatequerybuilderintegration.BaseTestCase
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils

class ColumnsCompletionTest : BaseTestCase() {
    private fun completeFor(
        from: String,
        prefix: String,
        method: String,
        argument: Int,
        completionType: CompletionType = CompletionType.BASIC
    ) {
        configureQueryBuilderMethod(from, prefix, method, argument)
        myFixture.complete(completionType)
    }

    fun testCompletesSchemasAndTables() {
        val table = DasUtil.getTables(db)
            .filter { !it.isSystem }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table)

        val expected = columns.map { it.name } + // All selected table columns
                listOf(
                    table.name,                 // Table itself
                    table.dasParent!!.name      // Table schema
                )

        val notExpected = schemas.filter { it != table.dasParent!!.name } + // All other schemas
                schemaTables.values.flatten().filter { it != table.name }   // All other tables

        LaravelUtils.BuilderTableColumnsParams.forEach { method, params ->
            params.forEach { param ->
                completeFor(table.name, "", method, param)

                assertCompletion(*expected.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesSchemaTables() {
        val schema = schemas.first()
        val table = schemaTables[schema]!!.first()
        val expected = schemaTables[schema]!!

        val notExpected = schemas.filterNot { it == schema } +                                 // All other schemas
                schemaTables.entries.filterNot { it.key == schema }.map { it.value }
                    .flatten() // Tables of other schemas

        LaravelUtils.BuilderTableColumnsParams.forEach { method, params ->
            params.forEach { param ->
                completeFor(table, "$schema.", method, param)

                assertCompletion(*expected.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesTableColumns() {
        val table = DasUtil.getTables(db)
            .filterNot { it.isSystem }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val lastTable = DasUtil.getTables(db)
            .filterNot { it.isSystem }
            .lastOrNull() ?: return fail("Did not find any tables.")

        val notExpected =
            schemas.filterNot { it == table.dasParent?.name } +                         // All other schemas
                    schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                        .flatten() +                                                                      // Tables of other schemas
                    DasUtil.getColumns(lastTable)
                        .filterNot { columns.contains(it.name) }
                        .map { it.name }                                                                  // Columns of other table

        LaravelUtils.BuilderTableColumnsParams.forEach { method, params ->
            params.forEach { param ->
                completeFor(table.name, "${table.name}.", method, param)

                assertCompletion(*columns.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesSchemaTableColumns() {
        val table = DasUtil.getTables(db)
            .filterNot { it.isSystem }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val lastTable = DasUtil.getTables(db)
            .filterNot { it.isSystem }
            .lastOrNull() ?: return fail("Did not find any tables.")

        val notExpected =
            schemas.filterNot { it == table.dasParent?.name } +                         // All other schemas
                    schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                        .flatten() +                                                                      // Tables of other schemas
                    DasUtil.getColumns(lastTable)
                        .filterNot { columns.contains(it.name) }
                        .map { it.name }                                                                  // Columns of other table

        LaravelUtils.BuilderTableColumnsParams.forEach { method, params ->
            params.forEach { param ->
                completeFor(table.name, "${table.dasParent?.name}.${table.name}.", method, param)

                assertCompletion(*columns.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesAliasColumns() {
        val table = DasUtil.getTables(db)
            .filterNot { it.isSystem }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val lastTable = DasUtil.getTables(db)
            .filterNot { it.isSystem }
            .lastOrNull() ?: return fail("Did not find any tables.")
        val alias = "${table.name}_alias"

        val notExpected =
            schemas.filterNot { it == table.dasParent?.name } +                         // All other schemas
                    schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                        .flatten() +                                                                      // Tables of other schemas
                    DasUtil.getColumns(lastTable)
                        .filterNot { columns.contains(it.name) }
                        .map { it.name }                                                                  // Columns of other table

        LaravelUtils.BuilderTableColumnsParams.forEach { method, params ->
            params.forEach { param ->
                completeFor("${table.name} as $alias", "$alias.", method, param)

                assertCompletion(*columns.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesColumnsAndSchemasTablesAfterSmartSearch() {
        val schema = schemas.first()
        val table = schemaTables[schema]!!.first()
        val expected = schemasAndTables + DasUtil.getTables(db).first { it.name == table }
            .getDasChildren(ObjectKind.COLUMN).map { it.name }

        val notExpected = listOf("failed_at")

        LaravelUtils.BuilderTableColumnsParams.forEach { method, params ->
            params.forEach { param ->
                completeFor(table, "", method, param, CompletionType.SMART)

                assertCompletion(*expected.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }
}