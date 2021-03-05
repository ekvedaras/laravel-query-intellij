package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import dev.ekvedaras.intellijilluminatequerybuilderintegration.BaseTestCase
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils

class SchemasAndTablesCompletionTest : BaseTestCase() {
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

            assertEquals(schemasAndTables.size, myFixture.lookupElementStrings?.size)
            assertCompletion(*schemasAndTables.toTypedArray())
        }
    }

    fun testItShowsOnlyTablesOfSchema() {
        schemas.forEach { schema ->
            LaravelUtils.BuilderTableMethods.forEach {
                completeTablesFor(schema, it)

                assertEquals(schemaTables[schema]!!.size, myFixture.lookupElementStrings?.size)
                assertCompletion(*schemaTables[schema]!!.toTypedArray())
            }
        }
    }
}
