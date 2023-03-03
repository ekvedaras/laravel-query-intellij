package dev.ekvedaras.laravelquery.integration.tests.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables

internal class TestsCompletionTest : BaseTestCase() {
    fun testItCompletesInAssertDatabaseHasCallTableProperty() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseHasCallTableProperty.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInAssertDatabaseHasCallColumnsPropertyArrayHashKey() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseHasCallColumnsProperty.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()

        Tables.users
            .expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns()
            .but().toHaveItsColumnsCompleted()
    }

    fun testItCompletesInAssertDatabaseHasCallColumnsPropertyArrayEntry() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseHasCallColumnsPropertyArrayEntry.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()

        Tables.users
            .expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns()
            .but().toHaveItsColumnsCompleted()
    }

    fun testItCompletesInAssertDatabaseCountCall() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseCountCall.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }
}
