package dev.ekvedaras.laravelquery.integration.query.completion

import dev.ekvedaras.laravelquery.BaseTestCase

internal class DatabaseElementCompletionTest : BaseTestCase() {
    fun testItCompletesNamespacesAndTablesInEmptyFromCall() {
        myFixture.configureByFile("integration/query/completion/inFromCall.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "testProject2", "users", "customers", "failed_jobs", "migrations")
    }

    fun testItCompletesNamespaceTablesInFromCall() {
        myFixture.configureByFile("integration/query/completion/inFromCallWithNamespace.php")
        myFixture.completeBasic()
        assertCompletion("users", "customers")
        assertNoCompletion("testProject1", "testProject2", "failed_jobs", "migrations")
    }
}
