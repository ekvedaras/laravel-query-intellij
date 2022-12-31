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

    fun testItCompletesUsersTableAndItsColumnsInSelectCall() {
        myFixture.configureByFile("integration/query/completion/inSelectCall.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesUsersTableAndItsColumnsInSelectCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inSelectCallUsingArray.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesUsersTableAndItsColumnsInSelectCallUsingMultipleParameters() {
        myFixture.configureByFile("integration/query/completion/inSelectCallUsingMultipleParameters.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesUsersTableAndItsColumnsInGetCall() {
        myFixture.configureByFile("integration/query/completion/inGetCall.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesUsersTableAndItsColumnsInGetCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inGetCallUsingArray.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesUsersTableAndItsColumnsInGetCallWithTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithTableName.php")
        myFixture.completeBasic()
        assertNoCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesUsersTableAndItsColumnsInGetCallWithAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithAliasedTableName.php")
        myFixture.completeBasic()
        assertNoCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesUsersTableAndItsColumnsInGetCallWithInlineAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithInlineAliasedTableName.php")
        myFixture.completeBasic()
        assertNoCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }
}
