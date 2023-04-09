package dev.ekvedaras.laravelquery.integration.schema.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables

internal class SchemaBuilderCompletionTest : BaseTestCase() {
    fun testItCompletesInCreateDatabaseMethodCall() {
        myFixture.configureByFile("integration/schema/completion/inCreateDatabaseCall.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).toBeCompleted()
        Tables.expect(myFixture).not().toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInDropDatabaseMethodCallOfNewDatabase() {
        myFixture.configureByFile("integration/schema/completion/inDropCallOfNewNamespace.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).toBeCompleted().withNewNamespace("new_database")
        Tables.expect(myFixture).not().toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInCreateMethodCall() {
        myFixture.configureByFile("integration/schema/completion/inCreateCallOfAnonymousMigration.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInDropMethodCallOfNewTable() {
        myFixture.configureByFile("integration/schema/completion/inDropCallOfNewTable.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).toBeCompleted().withNewTable("new_table")
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInHasTableMethodCall() {
        myFixture.configureByFile("integration/schema/completion/inHasTableCall.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInHasColumnMethodCallTableParameter() {
        myFixture.configureByFile("integration/schema/completion/inHasColumnCallTableParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInHasColumnMethodCallColumnParameter() {
        myFixture.configureByFile("integration/schema/completion/inHasColumnCallColumnParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted()
            .withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInHasColumnsMethodCallTableParameter() {
        myFixture.configureByFile("integration/schema/completion/inHasColumnsCallTableParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInHasColumnsMethodCallColumnParameterArrayEntry() {
        myFixture.configureByFile("integration/schema/completion/inHasColumnsCallColumnParameterArrayEntry.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted()
            .withoutOtherTables().andTheirColumns()
    }

    fun testItDoesNotCompleteInHasColumnsMethodCallColumnParameterString() {
        myFixture.configureByFile("integration/schema/completion/inHasColumnsCallColumnParameterString.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).not().toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInDropColumnsMethodCallTableParameter() {
        myFixture.configureByFile("integration/schema/completion/inDropColumnsCallTableParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInDropColumnsMethodCallColumnParameterArrayEntry() {
        myFixture.configureByFile("integration/schema/completion/inDropColumnsCallColumnParameterArrayEntry.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted()
            .withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInDropColumnsMethodCallColumnParameterString() {
        myFixture.configureByFile("integration/schema/completion/inDropColumnsCallColumnParameterString.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted()
            .withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesNewlyAddedColumnsInDropColumnsMethodCallColumnParameterString() {
        myFixture.configureByFile("integration/schema/completion/inDropColumnsCallColumnParameterStringInDownMethod.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted().withNewColumn("new_column")
            .withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesTablesInRenameMethodCallFromParameterInUpMethod() {
        myFixture.configureByFile("integration/schema/completion/inRenameCallFromParameterInUpMethod.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesTablesInRenameMethodCallToParameterInUpMethod() {
        myFixture.configureByFile("integration/schema/completion/inRenameCallToParameterInUpMethod.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesTablesInRenameMethodCallFromParameterInDownMethod() {
        myFixture.configureByFile("integration/schema/completion/inRenameCallFromParameterInDown.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).toBeCompleted().withNewTable("users1")
        Columns.expect(myFixture).not().toBeCompleted()
    }
}
