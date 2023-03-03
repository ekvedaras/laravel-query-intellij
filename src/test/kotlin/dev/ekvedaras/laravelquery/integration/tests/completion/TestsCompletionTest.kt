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

    fun testItCompletesInAssertDatabaseEmptyCall() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseEmptyCall.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInAssertSoftDeletedCallTableParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertSoftDeletedCallTableParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInAssertSoftDeletedCallDataFirstEntryParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertSoftDeletedCallDataFirstEntryParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()

        Tables.users
            .expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns()
            .but().toHaveItsColumnsCompleted()
    }

    fun testItCompletesInAssertSoftDeletedCallDataSecondEntryParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertSoftDeletedCallDataSecondEntryParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).not().toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInAssertSoftDeletedCallDataKeyParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertSoftDeletedCallDataKeyParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()

        Tables.users
            .expect(myFixture)
            .not().toBeCompleted().asWellAsOthers().andTheirColumns()
            .but().toHaveItsColumnsCompleted()
    }

    fun testItCompletesInAssertSoftDeletedCallDataValueParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertSoftDeletedCallDataValueParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).not().toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }

    fun testItCompletesInAssertNotSoftDeletedCallTableParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertNotSoftDeletedCallTableParameter.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }
}
