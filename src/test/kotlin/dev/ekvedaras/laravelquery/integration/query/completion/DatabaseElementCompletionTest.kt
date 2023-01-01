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

    fun testItCompletesInSelectCall() {
        myFixture.configureByFile("integration/query/completion/inSelectCall.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInSelectCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inSelectCallUsingArray.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInSelectCallUsingMultipleParameters() {
        myFixture.configureByFile("integration/query/completion/inSelectCallUsingMultipleParameters.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCall() {
        myFixture.configureByFile("integration/query/completion/inGetCall.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inGetCallUsingArray.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallWithTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithTableName.php")
        myFixture.completeBasic()
        assertNoCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallWithAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithAliasedTableName.php")
        myFixture.completeBasic()
        assertNoCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallWithInlineAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithInlineAliasedTableName.php")
        myFixture.completeBasic()
        assertNoCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallWithNamespaceAndTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithNamespaceAndTableName.php")
        myFixture.completeBasic()
        assertNoCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallWithNamespaceName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithNamespaceName.php")
        myFixture.completeBasic()
        assertCompletion("users", "customers")
        assertNoCompletion("email", "first_name")
        assertNoCompletion("testProject1", "testProject2", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallWithJoinedTable() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithJoinedTable.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users", "customers")
        assertCompletion("email", "first_name", "billable_id")
        assertNoCompletion("testProject2", "failed_jobs", "migrations")
        assertNoCompletion("connection", "migration")
    }

    fun testItCompletesInGetCallWithJoinedTableWithAlias() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithJoinedTableWithAlias.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users", "c1")
        assertCompletion("email", "first_name", "billable_id")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("connection", "migration")
    }

    fun testItCompletesInJoinCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inJoinCallFirstColumn.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users", "customers")
        assertCompletion("email", "first_name", "billable_id")
        assertNoCompletion("testProject2", "failed_jobs", "migrations")
        assertNoCompletion("connection", "migration")
    }

    fun testItCompletesInJoinCallSecondColumn() {
        myFixture.configureByFile("integration/query/completion/inJoinCallSecondColumn.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users", "customers")
        assertCompletion("email", "first_name", "billable_id")
        assertNoCompletion("testProject2", "failed_jobs", "migrations")
        assertNoCompletion("connection", "migration")
    }

    fun testItCompletesInJoinCallSecondColumnWithOperator() {
        myFixture.configureByFile("integration/query/completion/inJoinCallSecondColumnWithOperator.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users", "customers")
        assertCompletion("email", "first_name", "billable_id")
        assertNoCompletion("testProject2", "failed_jobs", "migrations")
        assertNoCompletion("connection", "migration")
    }

    fun testItCompletesInGetCallWithMultipleQueryStatements() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithMultipleQueryStatements.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }
}
