package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import dev.ekvedaras.intellijilluminatequerybuilderintegration.BaseTestCase

class TableCompletionTest : BaseTestCase() {
    fun testCompletesSchemasAndTables() {
        myFixture.configureByFile("completion/schemasAndTables.php")

        assertCompletion(
            "testProject1", "testProject2",
            "users", "customers", "failed_jobs", "migrations"
        )

        assert(myFixture.lookupElementStrings!!.size == 6)
    }
}