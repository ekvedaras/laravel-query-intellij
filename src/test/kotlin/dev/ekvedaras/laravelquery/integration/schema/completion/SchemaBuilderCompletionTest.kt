package dev.ekvedaras.laravelquery.integration.schema.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.DatabaseElementsExpectation.Companion.expect
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables
import dev.ekvedaras.laravelquery.support.Tables.Companion.expect

internal class SchemaBuilderCompletionTest : BaseTestCase() {
    fun testItCompletesInCreateDatabaseMethodCall() {
        myFixture.configureByFile("integration/schema/completion/inCreateDatabaseCall.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInDropDatabaseMethodCallOfNewDatabase() {
        myFixture.configureByFile("integration/schema/completion/inDropCallOfNewNamespace.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.toBeCompleted().withNewNamespace("new_database")
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInCreateMethodCall() {
        myFixture.configureByFile("integration/schema/completion/inCreateCallOfAnonymousMigration.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInDropMethodCallOfNewTable() {
        myFixture.configureByFile("integration/schema/completion/inDropCallOfNewTable.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.toBeCompleted().withNewTable("new_table")
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInHasTableMethodCall() {
        myFixture.configureByFile("integration/schema/completion/inHasTableCall.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInHasColumnMethodCallTableParameter() {
        myFixture.configureByFile("integration/schema/completion/inHasColumnCallTableParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInHasColumnMethodCallColumnParameter() {
        myFixture.configureByFile("integration/schema/completion/inHasColumnCallColumnParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted()
            .withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInHasColumnsMethodCallTableParameter() {
        myFixture.configureByFile("integration/schema/completion/inHasColumnsCallTableParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInHasColumnsMethodCallColumnParameterArrayEntry() {
        myFixture.configureByFile("integration/schema/completion/inHasColumnsCallColumnParameterArrayEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted()
            .withoutOtherTables().andTheirColumns()
    }

    fun testItDoesNotCompleteInHasColumnsMethodCallColumnParameterString() {
        myFixture.configureByFile("integration/schema/completion/inHasColumnsCallColumnParameterString.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInDropColumnsMethodCallTableParameter() {
        myFixture.configureByFile("integration/schema/completion/inDropColumnsCallTableParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInDropColumnsMethodCallColumnParameterArrayEntry() {
        myFixture.configureByFile("integration/schema/completion/inDropColumnsCallColumnParameterArrayEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted()
            .withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInDropColumnsMethodCallColumnParameterString() {
        myFixture.configureByFile("integration/schema/completion/inDropColumnsCallColumnParameterString.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted()
            .withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesNewlyAddedColumnsInDropColumnsMethodCallColumnParameterString() {
        myFixture.configureByFile("integration/schema/completion/inDropColumnsCallColumnParameterStringInDownMethod.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted().withNewColumn("new_column")
            .withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesTablesInRenameMethodCallFromParameterInUpMethod() {
        myFixture.configureByFile("integration/schema/completion/inRenameCallFromParameterInUpMethod.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesTablesInRenameMethodCallToParameterInUpMethod() {
        myFixture.configureByFile("integration/schema/completion/inRenameCallToParameterInUpMethod.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesTablesInRenameMethodCallFromParameterInDownMethod() {
        myFixture.configureByFile("integration/schema/completion/inRenameCallFromParameterInDown.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.toBeCompleted().withNewTable("users1")
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }
}
