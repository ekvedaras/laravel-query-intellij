package dev.ekvedaras.laravelquery.integration.tests.completion

import dev.ekvedaras.laravelquery.BaseTestCase

internal class TestsCompletionTest : BaseTestCase() {
    fun testItCompletesInAssertDatabaseHasCallTableProperty()
    {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseHasCallTableProperty.php")
        myFixture.completeBasic()

        assertCompletion("testProject1", "testProject2")
        assertCompletion("users", "customers", "failed_jobs", "migrations")
        assertNoCompletion("id", "email", "billable_id")
    }

    fun testItCompletesInAssertDatabaseHasCallColumnsPropertyArrayHashKey() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseHasCallColumnsProperty.php")
        myFixture.completeBasic()

        assertCompletion("id", "email")
        assertNoCompletion("billable_id")
        assertNoCompletion("testProject1", "testProject2")
        assertNoCompletion("users", "customers", "failed_jobs", "migrations")
    }

    fun testItCompletesInAssertDatabaseHasCallColumnsPropertyArrayEntry() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseHasCallColumnsPropertyArrayEntry.php")
        myFixture.completeBasic()

        assertCompletion("id", "email")
        assertNoCompletion("billable_id")
        assertNoCompletion("testProject1", "testProject2")
        assertNoCompletion("users", "customers", "failed_jobs", "migrations")
    }
}
