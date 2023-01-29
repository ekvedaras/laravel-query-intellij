package dev.ekvedaras.laravelquery.integration.query.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables

internal class QueryCompletionTest : BaseTestCase() {
    fun testItCompletesNamespacesAndTablesInEmptyFromCall() {
        myFixture.configureByFile("integration/query/completion/inFromCall.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesNamespaceTablesInFromCall() {
        myFixture.configureByFile("integration/query/completion/inFromCallWithNamespace.php")
        myFixture.completeBasic()

        Namespaces.testProject1
            .expect(myFixture)
            .not().toBeCompleted().asWellAsOthers()
            .but().toHaveTablesCompleted().onlyFromThisNamespace()
            .and().withoutAnyColumns()
    }

    fun testItCompletesInSelectCall() {
        myFixture.configureByFile("integration/query/completion/inSelectCall.php")
        myFixture.completeBasic()

        Namespaces.testProject1
            .expect(myFixture)
            .toBeCompleted()
            .exceptOthers()

        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .and().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInSelectCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inSelectCallUsingArray.php")
        myFixture.completeBasic()

        Namespaces.testProject1
            .expect(myFixture)
            .toBeCompleted()
            .exceptOthers()

        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .and().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInSelectCallUsingMultipleParameters() {
        myFixture.configureByFile("integration/query/completion/inSelectCallUsingMultipleParameters.php")
        myFixture.completeBasic()

        Namespaces.testProject1
            .expect(myFixture)
            .toBeCompleted()
            .exceptOthers()

        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .and().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCall() {
        myFixture.configureByFile("integration/query/completion/inGetCall.php")
        myFixture.completeBasic()

        Namespaces.testProject1
            .expect(myFixture)
            .toBeCompleted()
            .exceptOthers()

        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .and().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inGetCallUsingArray.php")
        myFixture.completeBasic()

        Namespaces.testProject1
            .expect(myFixture)
            .toBeCompleted()
            .exceptOthers()

        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .and().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallWithTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithTableName.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()

        Tables.users
            .expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns()
            .but().toHaveItsColumnsCompleted()
    }

    fun testItCompletesColumnsInGetCallWithAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithAliasedTableName.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()

        Tables.users
            .expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns()
            .but().toHaveItsColumnsCompleted()
    }

    fun testItCompletesInGetCallWithAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inEmptyGetCallWithAliasedTableName.php")
        myFixture.completeBasic()

        Namespaces.testProject1
            .expect(myFixture)
            .toBeCompleted()
            .exceptOthers()

        Tables.users
            .expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns()
            .but().toHaveItsColumnsCompleted()
            .and().toHaveAliasCompleted("u1")
    }

    fun testItCompletesInGetCallWithInlineAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithInlineAliasedTableName.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()

        Tables.users
            .expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns().andAlias("u1")
            .but().toHaveItsColumnsCompleted()
    }

    fun testItCompletesInGetCallWithNamespaceAndTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithNamespaceAndTableName.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()

        Tables.users
            .expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns()
            .but().toHaveItsColumnsCompleted()
    }

    fun testItCompletesInGetCallWithNamespaceName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithNamespaceName.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()

        Tables.users
            .expect(myFixture)
            .toBeCompleted()
            .asWellAsOthersFromSameNamespace() // todo: should actually be without other tables

        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInGetCallWithJoinedTable() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithJoinedTable.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.users.expect(myFixture).toBeCompleted().withColumns()
        Tables.customers.expect(myFixture).toBeCompleted().withColumns()

        Tables.migrations.expect(myFixture).not().toBeCompleted().withColumns()
        Tables.failed_jobs.expect(myFixture).not().toBeCompleted().withColumns()
    }

    fun testItCompletesInGetCallWithJoinedTableWithAlias() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithJoinedTableWithAlias.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.users.expect(myFixture).toBeCompleted().withColumns()
        Tables.customers
            .expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveAliasCompleted("c1").withColumns()

        Tables.migrations.expect(myFixture).not().toBeCompleted().withColumns()
        Tables.failed_jobs.expect(myFixture).not().toBeCompleted().withColumns()
    }

    fun testItCompletesInJoinCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inJoinCallFirstColumn.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.users.expect(myFixture).toBeCompleted().withColumns()
        Tables.customers.expect(myFixture).toBeCompleted().withColumns()

        Tables.migrations.expect(myFixture).not().toBeCompleted().withColumns()
        Tables.failed_jobs.expect(myFixture).not().toBeCompleted().withColumns()
    }

    fun testItCompletesInLeftJoinCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inLeftJoinCallFirstColumn.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.users.expect(myFixture).toBeCompleted().withColumns()
        Tables.customers.expect(myFixture).toBeCompleted().withColumns()

        Tables.migrations.expect(myFixture).not().toBeCompleted().withColumns()
        Tables.failed_jobs.expect(myFixture).not().toBeCompleted().withColumns()
    }

    fun testItCompletesInRightJoinCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inRightJoinCallFirstColumn.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.users.expect(myFixture).toBeCompleted().withColumns()
        Tables.customers.expect(myFixture).toBeCompleted().withColumns()

        Tables.migrations.expect(myFixture).not().toBeCompleted().withColumns()
        Tables.failed_jobs.expect(myFixture).not().toBeCompleted().withColumns()
    }

    fun testItCompletesInCrossJoinCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inCrossJoinCallFirstColumn.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.users.expect(myFixture).toBeCompleted().withColumns()
        Tables.customers.expect(myFixture).toBeCompleted().withColumns()

        Tables.migrations.expect(myFixture).not().toBeCompleted().withColumns()
        Tables.failed_jobs.expect(myFixture).not().toBeCompleted().withColumns()
    }

    fun testItCompletesInJoinCallSecondColumn() {
        myFixture.configureByFile("integration/query/completion/inJoinCallSecondColumn.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.users.expect(myFixture).toBeCompleted().withColumns()
        Tables.customers.expect(myFixture).toBeCompleted().withColumns()

        Tables.migrations.expect(myFixture).not().toBeCompleted().withColumns()
        Tables.failed_jobs.expect(myFixture).not().toBeCompleted().withColumns()
    }

    fun testItCompletesInJoinCallSecondColumnWithOperator() {
        myFixture.configureByFile("integration/query/completion/inJoinCallSecondColumnWithOperator.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.users.expect(myFixture).toBeCompleted().withColumns()
        Tables.customers.expect(myFixture).toBeCompleted().withColumns()

        Tables.migrations.expect(myFixture).not().toBeCompleted().withColumns()
        Tables.failed_jobs.expect(myFixture).not().toBeCompleted().withColumns()
    }

    fun testItCompletesInJoinClosureOnCall() {
        myFixture.configureByFile("integration/query/completion/inJoinClosureOnCall.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.users.expect(myFixture).toBeCompleted().withColumns()
        Tables.customers.expect(myFixture).toBeCompleted().withColumns()

        Tables.migrations.expect(myFixture).not().toBeCompleted().withColumns()
        Tables.failed_jobs.expect(myFixture).not().toBeCompleted().withColumns()
    }

    fun testItCompletesInGetCallWithMultipleQueryStatements() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithMultipleQueryStatements.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQuery() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQuery.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWhenModelHasTableField() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWhenModelHasTableField.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWhenParentModelHasTableField() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWhenParentModelHasTableField.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWhenCurrentClassHasTableButExtendsOtherModel() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWhenCurrentClassHasTableButExtendsOtherModel.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.customers
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWithMultipleStatements() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWithMultipleStatements.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWithNewInstanceCreation() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWithNewInstanceCreation.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWithNewInstanceCreationWithMultipleStatements() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWithNewInstanceCreationWithMultipleStatements.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesColumnsInCreateCallArrayHashKey() {
        myFixture.configureByFile("integration/query/completion/inCreateCallArrayHashKey.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns()
            .but().toHaveItsColumnsCompleted()
    }

    fun testItCompletesColumnsInCreateCallArrayEntry() {
        myFixture.configureByFile("integration/query/completion/inCreateCallArrayEntry.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns()
            .but().toHaveItsColumnsCompleted()
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

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inWhereCallUsingArray.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }

    fun testItDoesNotCompleteInGetCallUsingNestedArrayLikeWhere() {
        myFixture.configureByFile("integration/query/completion/inGetCallUsingNestedArrayLikeWhere.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).not().toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInWhereCallInsideRelationClosure() {
        myFixture.configureByFile("integration/query/completion/inWhereCallInsideRelationClosure.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.customers
            .expect(myFixture)
            .toBeCompleted().withColumns().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereCallOnAQueryWithARelationClosure() {
        myFixture.configureByFile("integration/query/completion/inWhereCallOnAQueryWithARelationClosure.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()

        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereCallStaticallyOnModel() {
        myFixture.configureByFile("integration/query/completion/inWhereCallStaticallyOnModel.php")
        myFixture.completeBasic()

        Namespaces.testProject1.expect(myFixture).toBeCompleted().exceptOthers()
        Tables.users
            .expect(myFixture)
            .toBeCompleted().withColumns()
            .but().withoutOtherTables().andTheirColumns()
    }
}
