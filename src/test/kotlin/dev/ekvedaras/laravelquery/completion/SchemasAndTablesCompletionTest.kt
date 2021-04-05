package dev.ekvedaras.laravelquery.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.utils.LaravelUtils

internal class SchemasAndTablesCompletionTest : BaseTestCase() {
    private fun completeAllFor(method: String) {
        myFixture.configureByText(
            "test.php",
            "<?php (new Illuminate\\Database\\Query\\Builder())->$method('<caret>')"
        )
        myFixture.completeBasic()
    }

    private fun completeTablesFor(schema: String, method: String) {
        myFixture.configureByText(
            "test.php",
            "<?php (new Illuminate\\Database\\Query\\Builder())->$method('$schema.<caret>')"
        )
        myFixture.completeBasic()
    }

    fun testCompletesSchemasAndTables() {
        LaravelUtils.BuilderTableMethods.forEach { method ->
            completeAllFor(method)

            if (LaravelUtils.BuilderSchemaMethods.contains(method)) {
                assertEquals(schemas.size, myFixture.lookupElementStrings?.size)
                assertCompletion(*schemas.toTypedArray())
            } else {
                assertEquals(schemasAndTables.size, myFixture.lookupElementStrings?.size)
                assertCompletion(*schemasAndTables.toTypedArray())
            }
        }
    }

    fun testItShowsOnlyTablesOfSchema() {
        schemas.forEach { schema ->
            LaravelUtils.BuilderTableMethods.forEach {
                completeTablesFor(schema, it)

                assertEquals(schemaTables[schema]?.size, myFixture.lookupElementStrings?.size)
                assertCompletion(*schemaTables[schema]?.toTypedArray() ?: return fail("Failed to get schema tables"))
            }
        }
    }
}
