package dev.ekvedaras.laravelquery.integration.schema.blueprint.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.DatabaseElementsExpectation.Companion.expect
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables
import dev.ekvedaras.laravelquery.support.Tables.Companion.expect

internal class BlueprintCompletionTest : BaseTestCase() {
    fun testItCompletesInStringMethodCallOfExistingTable() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inStringCallOfCreateCall.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInDropColumnMethodCallStringParameter() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inDropColumnCallStringParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInDropColumnMethodCallSecondStringParameter() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inDropColumnCallSecondStringParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInDropColumnMethodCallArrayFirstEntry() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inDropColumnCallArrayFirstEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInDropColumnMethodCallStringParameterWithNewColumn() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inDropColumnCallStringParameterWithNewColumn.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted().withNewColumn("new_column")
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInRenameColumnsMethodCallFromParameter() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inRenameColumnCallFromParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInRenameColumnsMethodCallToParameter() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inRenameColumnCallToParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInRenameColumnsMethodCallFromParameterDownMethod() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inRenameColumnCallFromParameterDownMethod.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted().withNewColumn("old_email")
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInForeignIdForMethodCall() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inForeignIdForCall.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInAddColumnMethodCall() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inAddColumnCall.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInIndexMethodCallColumnsParameter() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inIndexCallColumnsParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInIndexMethodCallColumnsParameterInArray() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inIndexCallColumnsParameterInArray.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect(Tables.users)
            .not.toBeCompleted()
            .but.toHaveItsColumnsCompleted()
            .and.withoutOtherTables().andTheirColumns()
    }

    fun testItCompletesInIndexMethodCallIndexParameter() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inIndexCallIndexParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()

        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers()
            .but.toHaveItsIndexesCompleted().withNewIndex("users_first_name_email_index")
    }
}
