package dev.ekvedaras.laravelquery.integration.query.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.Columns
import dev.ekvedaras.laravelquery.Namespaces
import dev.ekvedaras.laravelquery.Tables

internal class QueryCompletionTest : BaseTestCase() {
    fun testItCompletesNamespacesAndTablesInEmptyFromCall() {
        myFixture.configureByFile("integration/query/completion/inFromCall.php")
        myFixture.completeBasic()
        Namespaces.assertAllSuggested(myFixture)
        Tables.assertAllSuggested(myFixture)
        Columns.assertNoneAreSuggested(myFixture)
    }

    fun testItCompletesNamespaceTablesInFromCall() {
        myFixture.configureByFile("integration/query/completion/inFromCallWithNamespace.php")
        myFixture.completeBasic()
        Namespaces.testProject1.assertIsTheOnlyOneSuggested(myFixture)
        Tables.assertNoneAreSuggested(myFixture)
        Columns.assertNoneAreSuggested(myFixture)
    }

    fun testItCompletesInSelectCall() {
        myFixture.configureByFile("integration/query/completion/inSelectCall.php")
        myFixture.completeBasic()
        Namespaces.testProject1.assertIsTheOnlyOneSuggested(myFixture)
        Tables.users
            .assertIsTheOnlyOneSuggested(myFixture)
            .assertColumnsAreSuggestedOnlyForThisTable(myFixture)
    }

    fun testItCompletesInSelectCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inSelectCallUsingArray.php")
        myFixture.completeBasic()

        Namespaces.testProject1.assertIsTheOnlyOneSuggested(myFixture)
        Tables.users
            .assertIsTheOnlyOneSuggested(myFixture)
            .assertColumnsAreSuggestedOnlyForThisTable(myFixture)
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

    fun testItCompletesColumnsInGetCallWithAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithAliasedTableName.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallWithAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inEmptyGetCallWithAliasedTableName.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "u1")
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

    fun testItCompletesInGetCallOfEloquentQuery() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQuery.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallOfEloquentQueryWhenModelHasTableField() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWhenModelHasTableField.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallOfEloquentQueryWhenParentModelHasTableField() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWhenParentModelHasTableField.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallOfEloquentQueryWithMultipleStatements() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWithMultipleStatements.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallOfEloquentQueryWithNewInstanceCreation() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWithNewInstanceCreation.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInGetCallOfEloquentQueryWithNewInstanceCreationWithMultipleStatements() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWithNewInstanceCreationWithMultipleStatements.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesColumnsInCreateCallArrayHashKey() {
        myFixture.configureByFile("integration/query/completion/inCreateCallArrayHashKey.php")
        myFixture.completeBasic()
        assertCompletion("email", "first_name")
        assertNoCompletion("billable_id")
        assertNoCompletion("testProject1", "testProject2", "users", "customers", "failed_jobs", "migrations")
    }

    fun testItCompletesColumnsInCreateCallArrayEntry() {
        myFixture.configureByFile("integration/query/completion/inCreateCallArrayEntry.php")
        myFixture.completeBasic()
        assertCompletion("email", "first_name")
        assertNoCompletion("billable_id")
        assertNoCompletion("testProject1", "testProject2", "users", "customers", "failed_jobs", "migrations")
    }

    fun testItDoesNotCompleteColumnsInCreateCallArrayHashValue() {
        myFixture.configureByFile("integration/query/completion/inCreateCallArrayHashValue.php")
        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElements ?: arrayOf())
    }

    fun testItDoesNotCompleteColumnsInNonModelClasses() {
        myFixture.configureByFile("integration/query/completion/inCreateCallOfNonModelClass.php")
        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElements ?: arrayOf())
    }

    fun testItCompletesInWhereCallWithinModelScope() {
        myFixture.configureByFile("integration/query/completion/inWhereCallWithinModelScope.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }

    fun testItCompletesInWhereCallStaticallyOnModel() {
        myFixture.configureByFile("integration/query/completion/inWhereCallStaticallyOnModel.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "users")
        assertCompletion("email", "first_name")
        assertNoCompletion("testProject2", "customers", "failed_jobs", "migrations")
        assertNoCompletion("billable_id", "connection", "migration")
    }
}
