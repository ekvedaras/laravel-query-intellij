package dev.ekvedaras.laravelquery.integration.query.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.DatabaseElementsExpectation.Companion.expect
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Namespaces.Companion.expect
import dev.ekvedaras.laravelquery.support.Tables
import dev.ekvedaras.laravelquery.support.Tables.Companion.expect

internal class QueryCompletionTest : BaseTestCase() {
    fun testItCompletesNamespacesAndTablesInEmptyFromCall() {
        myFixture.configureByFile("integration/query/completion/inFromCall.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesNamespaceTablesInFromCall() {
        myFixture.configureByFile("integration/query/completion/inFromCallWithNamespace.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1)
            .not.toBeCompleted().asWellAsOthers()
            .but.toHaveTablesCompleted().onlyFromThisNamespace()
            .and.withoutAnyColumns()
    }

    fun testItCompletesInSelectCall() {
        myFixture.configureByFile("integration/query/completion/inSelectCall.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInAddSelectCall() {
        myFixture.configureByFile("integration/query/completion/inAddSelectCall.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInSelectCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inSelectCallUsingArray.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInSelectCallUsingMultipleParameters() {
        myFixture.configureByFile("integration/query/completion/inSelectCallUsingMultipleParameters.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCall() {
        myFixture.configureByFile("integration/query/completion/inGetCall.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereRowColumnsCall() {
        myFixture.configureByFile("integration/query/completion/inWhereRowValuesCall.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInForPageBeforeIdCall() {
        myFixture.configureByFile("integration/query/completion/inForPageBeforeIdCall.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItDoesNotCompleteInForPageBeforeIdCallFirstParameter() {
        myFixture.configureByFile("integration/query/completion/inForPageBeforeIdCallFirstParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInFindCall() {
        myFixture.configureByFile("integration/query/completion/inFindCall.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInFirstOrNewCallAttributesFirstEntry() {
        myFixture.configureByFile("integration/query/completion/inFirstOrNewCallAttributesFirstEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInFirstOrNewCallValuesFirstEntry() {
        myFixture.configureByFile("integration/query/completion/inFirstOrNewCallValuesFirstEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInPaginateCall() {
        myFixture.configureByFile("integration/query/completion/inPaginateCall.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInPluckCallColumn() {
        myFixture.configureByFile("integration/query/completion/inPluckCallColumn.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInPluckCallKey() {
        myFixture.configureByFile("integration/query/completion/inPluckCallKey.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInInsertCallTopArrayEntry() {
        myFixture.configureByFile("integration/query/completion/inInsertTopArrayEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInInsertCallTopArrayKey() {
        myFixture.configureByFile("integration/query/completion/inInsertTopArrayKey.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItDoesNotCompleteInInsertCallTopArrayValue() {
        myFixture.configureByFile("integration/query/completion/inInsertTopArrayValue.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }


    fun testItCompletesInInsertCallInnerArrayEntry() {
        myFixture.configureByFile("integration/query/completion/inInsertInnerArrayEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInInsertCallInnerArraySecondEntry() {
        myFixture.configureByFile("integration/query/completion/inInsertInnerArraySecondEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInInsertCallInnerArrayKey() {
        myFixture.configureByFile("integration/query/completion/inInsertInnerArrayKey.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItDoesNotCompleteInInsertCallInnerArrayValue() {
        myFixture.configureByFile("integration/query/completion/inInsertInnerArrayValue.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInInsertGetIdCallArrayEntry() {
        myFixture.configureByFile("integration/query/completion/inInsertGetIdCallArrayEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInInsertGetIdCallArrayKey() {
        myFixture.configureByFile("integration/query/completion/inInsertGetIdCallArrayKey.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInInsertGetIdCallSequence() {
        myFixture.configureByFile("integration/query/completion/inInsertGetIdCallSequence.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInInsertUsingCallArrayEntry() {
        myFixture.configureByFile("integration/query/completion/inInsertUsingCallArrayEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItDoesNotCompleteInInsertUsingCallArrayKey() {
        myFixture.configureByFile("integration/query/completion/inInsertUsingCallArrayKey.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInUpdateOrInsertCallAttributesKey() {
        myFixture.configureByFile("integration/query/completion/inUpdateOrInsertCallAttributesKey.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInUpdateOrInsertCallValuesKey() {
        myFixture.configureByFile("integration/query/completion/inUpdateOrInsertCallValuesKey.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inGetCallUsingArray.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallWithTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithTableName.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()

        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesColumnsInGetCallWithAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithAliasedTableName.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()

        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesInGetCallWithAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inEmptyGetCallWithAliasedTableName.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
            .and.toHaveAliasCompleted("u1")
    }

    fun testItCompletesInGetCallWithInlineAliasedTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithInlineAliasedTableName.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()

        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns().andAlias("u1")
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesInGetCallWithNamespaceAndTableName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithNamespaceAndTableName.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()

        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesInGetCallWithNamespaceName() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithNamespaceName.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()

        myFixture.expect(Tables.users)
            .toBeCompleted()
            .asWellAsOthersFromSameNamespace() // todo: should actually be without other tables

        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInGetCallWithJoinedTable() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithJoinedTable.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers).toBeCompleted().withColumns()

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInGetCallWithJoinedTableWithAlias() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithJoinedTableWithAlias.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers)
            .not.toBeCompleted()
            .but.toHaveAliasCompleted("c1").withColumns()

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInJoinCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inJoinCallFirstColumn.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers).toBeCompleted().withColumns()

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInJoinSubCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inJoinSubCallFirstColumn.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()

        myFixture.expect(Tables.customers)
            .not.toBeCompleted().withColumns()
            .but.toHaveAliasCompleted("c2")

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInJoinWhereCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inJoinWhereCallFirstColumn.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers).toBeCompleted().withColumns()

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInLeftJoinCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inLeftJoinCallFirstColumn.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers).toBeCompleted().withColumns()

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInRightJoinCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inRightJoinCallFirstColumn.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers).toBeCompleted().withColumns()

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInCrossJoinCallFirstColumn() {
        myFixture.configureByFile("integration/query/completion/inCrossJoinCallFirstColumn.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers).toBeCompleted().withColumns()

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInJoinCallSecondColumn() {
        myFixture.configureByFile("integration/query/completion/inJoinCallSecondColumn.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers).toBeCompleted().withColumns()

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInJoinCallSecondColumnWithOperator() {
        myFixture.configureByFile("integration/query/completion/inJoinCallSecondColumnWithOperator.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers).toBeCompleted().withColumns()

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInJoinClosureOnCall() {
        myFixture.configureByFile("integration/query/completion/inJoinClosureOnCall.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers).toBeCompleted().withColumns()

        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInGetCallWithMultipleQueryStatements() {
        myFixture.configureByFile("integration/query/completion/inGetCallWithMultipleQueryStatements.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQuery() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQuery.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWhenModelHasTableField() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWhenModelHasTableField.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWhenParentModelHasTableField() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWhenParentModelHasTableField.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWhenCurrentClassHasTableButExtendsOtherModel() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWhenCurrentClassHasTableButExtendsOtherModel.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.customers)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWithMultipleStatements() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWithMultipleStatements.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWithNewInstanceCreation() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWithNewInstanceCreation.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallOfEloquentQueryWithNewInstanceCreationWithMultipleStatements() {
        myFixture.configureByFile("integration/query/completion/inGetCallOfEloquentQueryWithNewInstanceCreationWithMultipleStatements.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesColumnsInCreateCallArrayHashKey() {
        myFixture.configureByFile("integration/query/completion/inCreateCallArrayHashKey.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesColumnsInMakeCallArrayEntry() {
        myFixture.configureByFile("integration/query/completion/inMakeCallArrayEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesColumnsInCreateCallArrayEntry() {
        myFixture.configureByFile("integration/query/completion/inCreateCallArrayEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItDoesNotCompleteColumnsInCreateCallArrayHashValue() {
        myFixture.configureByFile("integration/query/completion/inCreateCallArrayHashValue.php")
        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElements ?: arrayOf())
    }

    fun testItCompleteColumnsInWhereColumnCallArrayHashValue() {
        myFixture.configureByFile("integration/query/completion/inWhereColumnCallArrayHashValue.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItDoesNotCompleteColumnsInNonModelClasses() {
        myFixture.configureByFile("integration/query/completion/inCreateCallOfNonModelClass.php")
        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElements ?: arrayOf())
    }

    fun testItCompletesInWhereCallWithinModelScope() {
        myFixture.configureByFile("integration/query/completion/inWhereCallWithinModelScope.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereCallUsingArray() {
        myFixture.configureByFile("integration/query/completion/inWhereCallUsingArray.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItDoesNotCompleteInGetCallUsingNestedArrayLikeWhere() {
        myFixture.configureByFile("integration/query/completion/inGetCallUsingNestedArrayLikeWhere.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInWhereCallUsingClosure() {
        myFixture.configureByFile("integration/query/completion/inWhereCallUsingClosure.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInFirstWhereCallUsingClosure() {
        myFixture.configureByFile("integration/query/completion/inFirstWhereCallUsingClosure.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereCallInsideWhenCall() {
        myFixture.configureByFile("integration/query/completion/inWhereCallInsideWhenCall.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereCallInsideWhenCallFalseClosure() {
        myFixture.configureByFile("integration/query/completion/inWhereCallInsideWhenCallFalseClosure.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInGetCallAfterWhenCallWithJoin() {
        myFixture.configureByFile("integration/query/completion/inGetCallAfterWhenCallWithJoin.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users).toBeCompleted().withColumns()
        myFixture.expect(Tables.customers).toBeCompleted().withColumns()
        myFixture.expect(Tables.migrations).not.toBeCompleted().withColumns()
        myFixture.expect(Tables.failed_jobs).not.toBeCompleted().withColumns()
    }

    fun testItCompletesInWhereCallInsideRelationClosure() {
        myFixture.configureByFile("integration/query/completion/inWhereCallInsideRelationClosure.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.customers)
            .toBeCompleted().withColumns().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereBetweenColumnsCallFirstParameter() {
        myFixture.configureByFile("integration/query/completion/inWhereBetweenColumnCallFirstParameter.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereBetweenColumnsCallSecondParameterFirstEntry() {
        myFixture.configureByFile("integration/query/completion/inWhereBetweenColumnCallSecondParameterFirstEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereBetweenColumnsCallSecondParameterSecondEntry() {
        myFixture.configureByFile("integration/query/completion/inWhereBetweenColumnCallSecondParameterSecondEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns().withoutOtherTables().andTheirColumns()
    }

    fun testItDoesNotCompleteInWhereBetweenColumnsCallSecondParameterThirdEntry() {
        myFixture.configureByFile("integration/query/completion/inWhereBetweenColumnCallSecondParameterThirdEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInWhereCallOnAQueryWithARelationClosure() {
        myFixture.configureByFile("integration/query/completion/inWhereCallOnAQueryWithARelationClosure.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()

        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereCallStaticallyOnModel() {
        myFixture.configureByFile("integration/query/completion/inWhereCallStaticallyOnModel.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInWhereColumnCallSecondParameter() {
        myFixture.configureByFile("integration/query/completion/inWhereColumnCallSecondParameter.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted().exceptOthers()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInCreateCallOnRelationQuery() {
        myFixture.configureByFile("integration/query/completion/inCreateCallOnRelationQuery.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.customers)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesInUpsertCallValuesArrayFirstEntry() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallValuesArrayFirstEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInUpsertCallValuesArrayKey() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallValuesArrayKey.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInUpsertCallValuesInnerArrayFirstEntry() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallValuesInnerArrayFirstEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInUpsertCallValuesInnerArraySecondEntry() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallValuesInnerArraySecondEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInUpsertCallValuesInnerArrayKey() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallValuesInnerArrayKey.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInUpsertCallValuesInnerArrayValue() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallValuesInnerArrayValue.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInUpsertCallUniqueByString() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallUniqueByString.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInUpsertCallUniqueByArrayFirstEntry() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallUniqueByArrayFirstEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInUpsertCallUniqueByArraySecondEntry() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallUniqueByArraySecondEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInUpsertCallUniqueByArrayKey() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallUniqueByArrayKey.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInUpsertCallUniqueByArrayValue() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallUniqueByArrayValue.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInUpsertCallUpdateString() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallUpdateString.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInUpsertCallUpdateArrayFirstEntry() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallUpdateArrayFirstEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInUpsertCallUpdateArraySecondEntry() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallUpdateArraySecondEntry.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted()
        myFixture.expect(Tables.users)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInUpsertCallUpdateArrayKey() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallUpdateArrayKey.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInUpsertCallUpdateArrayValue() {
        myFixture.configureByFile("integration/query/completion/inUpsertCallUpdateArrayValue.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInWithCallRelationString() {
        myFixture.configureByFile("integration/query/completion/inWithCallRelationString.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithCallRelationArrayFirstEntry() {
        myFixture.configureByFile("integration/query/completion/inWithCallRelationArrayFirstEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithCallRelationArraySecondEntry() {
        myFixture.configureByFile("integration/query/completion/inWithCallRelationArraySecondEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertNoCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithCallRelationArrayKey() {
        myFixture.configureByFile("integration/query/completion/inWithCallRelationArrayKey.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithCallRelationArrayValue() {
        myFixture.configureByFile("integration/query/completion/inWithCallRelationArrayValue.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertNoCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithCallRelationSecondString() {
        myFixture.configureByFile("integration/query/completion/inWithCallRelationSecondString.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInGetRelationCall() {
        myFixture.configureByFile("integration/query/completion/inGetRelationCall.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithoutCallRelationString() {
        myFixture.configureByFile("integration/query/completion/inWithoutCallRelationString.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithoutCallRelationArrayFirstEntry() {
        myFixture.configureByFile("integration/query/completion/inWithoutCallRelationArrayFirstEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithoutCallRelationArraySecondEntry() {
        myFixture.configureByFile("integration/query/completion/inWithoutCallRelationArraySecondEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithoutCallRelationArrayKey() {
        myFixture.configureByFile("integration/query/completion/inWithoutCallRelationArrayKey.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertNoCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithoutCallRelationArrayValue() {
        myFixture.configureByFile("integration/query/completion/inWithoutCallRelationArrayValue.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertNoCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInWithoutCallRelationSecondString() {
        myFixture.configureByFile("integration/query/completion/inWithoutCallRelationSecondString.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertCompletion("freeCustomers", "payingCustomers")
    }

    fun testItCompletesInNewQueryWithoutScopeCall() {
        myFixture.configureByFile("integration/query/completion/inNewQueryWithoutScopeCall.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()

        assertCompletion("active", "inActive")
    }

    fun testItCompletesInWhereCallOnRelationInsideModel() {
        myFixture.configureByFile("integration/query/completion/inWhereCallOnRelationInsideModel.php")
        myFixture.completeBasic()

        myFixture.expect(Namespaces.testProject1).toBeCompleted()
        myFixture.expect(Tables.customers)
            .toBeCompleted().withColumns()
            .but.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInHasManyCallForeignKey() {
        myFixture.configureByFile("integration/query/completion/inHasManyCallForeignKey.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.customers)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInHasManyCallLocalKey() {
        myFixture.configureByFile("integration/query/completion/inHasManyCallLocalKey.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }
}
