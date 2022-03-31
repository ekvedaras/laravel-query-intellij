package dev.ekvedaras.laravelquery.completion

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.utils.LaravelUtils

internal class ColumnsCompletionTest : BaseTestCase() {
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
        val table = DasUtil.getTables(dataSource())
            .filter { !it.isSystem }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table)

        val expected = columns.map { it.name } + // All selected table columns
            listOf(
                table.name, // Table itself
                table.dasParent?.name ?: return fail("Failed to load table schema") // Table schema
            )

        val notExpected = schemas.filter { it != table.dasParent?.name ?: it } + // All other schemas
            schemaTables.values.flatten().filter { it != table.name } // All other tables

        LaravelUtils.BuilderTableColumnsParams.entries.distinctBy { it.value }.forEach { entry ->
            entry.value.forEach { param ->
                completeFor(table.name, "", entry.key, param)

                assertCompletion(*expected.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesSchemaTables() {
        val schema = schemas.first()
        val table = schemaTables[schema]?.first() ?: return fail("Failed to find first table")
        val expected = schemaTables[schema] ?: return fail("Failed to find schema tables")

        val notExpected = schemas.filterNot { it == schema } + // All other schemas
            schemaTables.entries.filterNot { it.key == schema }.map { it.value }
                .flatten() // Tables of other schemas

        LaravelUtils.BuilderTableColumnsParams.entries.distinctBy { it.value }.forEach { entry ->
            entry.value.forEach { param ->
                completeFor(table, "$schema.", entry.key, param)

                assertCompletion(*expected.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesTableColumns() {
        val table = DasUtil.getTables(dataSource())
            .filterNot { it.isSystem }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val lastTable = DasUtil.getTables(dataSource())
            .filterNot { it.isSystem }
            .lastOrNull() ?: return fail("Did not find any tables.")

        val notExpected =
            schemas.filterNot { it == table.dasParent?.name } + // All other schemas
                schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                    .flatten() + // Tables of other schemas
                DasUtil.getColumns(lastTable)
                    .filterNot { columns.contains(it.name) }
                    .map { it.name } // Columns of other table

        LaravelUtils.BuilderTableColumnsParams.entries.distinctBy { it.value }.forEach { entry ->
            entry.value.forEach { param ->
                completeFor(table.name, "${table.name}.", entry.key, param)

                assertCompletion(*columns.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesSchemaTableColumns() {
        val table = DasUtil.getTables(dataSource())
            .filterNot { it.isSystem }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val lastTable = DasUtil.getTables(dataSource())
            .filterNot { it.isSystem }
            .lastOrNull() ?: return fail("Did not find any tables.")

        val notExpected =
            schemas.filterNot { it == table.dasParent?.name } + // All other schemas
                schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                    .flatten() + // Tables of other schemas
                DasUtil.getColumns(lastTable)
                    .filterNot { columns.contains(it.name) }
                    .map { it.name } // Columns of other table

        LaravelUtils.BuilderTableColumnsParams.entries.distinctBy { it.value }.forEach { entry ->
            entry.value.forEach { param ->
                completeFor(table.name, "${table.dasParent?.name}.${table.name}.", entry.key, param)

                assertCompletion(*columns.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesAliasColumns() {
        val table = DasUtil.getTables(dataSource())
            .filterNot { it.isSystem }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val lastTable = DasUtil.getTables(dataSource())
            .filterNot { it.isSystem }
            .lastOrNull() ?: return fail("Did not find any tables.")
        val alias = "${table.name}_alias"

        val notExpected =
            schemas.filterNot { it == table.dasParent?.name } + // All other schemas
                schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                    .flatten() + // Tables of other schemas
                DasUtil.getColumns(lastTable)
                    .filterNot { columns.contains(it.name) }
                    .map { it.name } // Columns of other table

        LaravelUtils.BuilderTableColumnsParams.entries.distinctBy { it.value }.forEach { entry ->
            entry.value.forEach { param ->
                completeFor("${table.name} as $alias", "$alias.", entry.key, param)

                assertCompletion(*columns.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesColumnsAndSchemasTablesAfterSmartSearch() {
        val schema = schemas.first()
        val table = schemaTables[schema]?.first() ?: return fail("Failed to find first table")
        val expected = schemasAndTables + DasUtil.getTables(dataSource()).first { it.name == table }
            .getDasChildren(ObjectKind.COLUMN).map { it.name }

        val notExpected = listOf("failed_at")

        LaravelUtils.BuilderTableColumnsParams.entries.distinctBy { it.value }.forEach { entry ->
            entry.value.forEach { param ->
                completeFor(table, "", entry.key, param, CompletionType.SMART)

                assertCompletion(*expected.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
        }
    }

    fun testCompletesColumnsInsideJoinClause() {
        val tables = DasUtil.getTables(dataSource()).filter {
            it.name == "users" || it.name == "customers"
        }

        val expected = tables.map { it.name } +
            (tables.first()?.getDasChildren(ObjectKind.COLUMN)?.map { it.name } ?: listOf<String>()) +
            (tables.last()?.getDasChildren(ObjectKind.COLUMN)?.map { it.name } ?: listOf<String>())

        val notExpected = listOf("failed_jobs", "migrations", "testProject2")

        myFixture.configureByFile("inspection/joinColumns.php")
        myFixture.completeBasic()

        assertCompletion(*expected.toList().toTypedArray())
        assertNoCompletion(*notExpected.toList().toTypedArray())
    }

    fun testCompletesTableColumnsTestAssertions() {
        val table = DasUtil.getTables(dataSource()).first { it.name == "users" }
        val columns = DasUtil.getColumns(table).map { it.name }
        val lastTable = DasUtil.getTables(dataSource()).first { it.name == "customers" }

        val notExpected =
            schemas.filterNot { it == table.dasParent?.name } + // All other schemas
                schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                    .flatten() + // Tables of other schemas
                DasUtil.getColumns(lastTable)
                    .filterNot { columns.contains(it.name) }
                    .map { it.name } // Columns of other table

        LaravelUtils.BuilderTableMethods
            .filter { it.startsWith("assert") }
            .filterNot { it === "assertDatabaseCount" }
            .forEach { method ->
                myFixture.configureByText(
                    "test.php",
                    "<?php class Test extends \\Tests\\TestCase { public function test_it_completes() { \$this->$method('users', ['<caret>']); } }"
                )
                myFixture.completeBasic()

                assertCompletion(*columns.toList().toTypedArray())
                assertNoCompletion(*notExpected.toList().toTypedArray())
            }
    }
}
