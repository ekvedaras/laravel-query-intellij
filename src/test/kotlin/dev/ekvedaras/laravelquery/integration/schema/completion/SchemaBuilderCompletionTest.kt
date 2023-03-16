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
}
