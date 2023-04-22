package dev.ekvedaras.laravelquery.integration.schema.blueprint.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables

internal class BlueprintCompletionTest : BaseTestCase() {
    fun testItCompletesInStringMethodCallOfExistingTable() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inStringCallOfCreateCall.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInDropColumnMethodCallStringParameter() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inDropColumnCallStringParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInDropColumnMethodCallSecondStringParameter() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inDropColumnCallSecondStringParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).not().toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInDropColumnMethodCallArrayFirstEntry() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inDropColumnCallArrayFirstEntry.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInDropColumnMethodCallStringParameterWithNewColumn() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inDropColumnCallStringParameterWithNewColumn.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted().withNewColumn("new_column")
            .and().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInRenameColumnsMethodCallFromParameter() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inRenameColumnCallFromParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInRenameColumnsMethodCallToParameter() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inRenameColumnCallToParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInRenameColumnsMethodCallFromParameterDownMethod() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inRenameColumnCallFromParameterDownMethod.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted().withNewColumn("old_email")
            .and().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInForeignIdForMethodCall() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inForeignIdForCall.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted()
            .and().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInAddColumnMethodCall() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inAddColumnCall.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted()
            .and().withoutOtherTables().andTheirColumns()
    }
}
