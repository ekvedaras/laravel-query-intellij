package dev.ekvedaras.laravelquery.integration.tests.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.DatabaseElementsExpectation.Companion.expect
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables
import dev.ekvedaras.laravelquery.support.Tables.Companion.expect

internal class TestsCompletionTest : BaseTestCase() {
    fun testItCompletesInAssertDatabaseHasCallTableProperty() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseHasCallTableProperty.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInAssertDatabaseHasCallColumnsPropertyArrayHashKey() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseHasCallColumnsProperty.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()

        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesInAssertDatabaseHasCallColumnsPropertyArrayEntry() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseHasCallColumnsPropertyArrayEntry.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()

        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesInAssertDatabaseCountCall() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseCountCall.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInAssertDatabaseEmptyCall() {
        myFixture.configureByFile("integration/tests/completion/inAssertDatabaseEmptyCall.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInAssertSoftDeletedCallTableParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertSoftDeletedCallTableParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInAssertSoftDeletedCallDataFirstEntryParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertSoftDeletedCallDataFirstEntryParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()

        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesInAssertSoftDeletedCallDataSecondEntryParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertSoftDeletedCallDataSecondEntryParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInAssertSoftDeletedCallDataKeyParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertSoftDeletedCallDataKeyParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()

        myFixture.expect(Tables.users)
            .not.toBeCompleted().asWellAsOthers().andTheirColumns()
            .but.toHaveItsColumnsCompleted()
    }

    fun testItCompletesInAssertSoftDeletedCallDataValueParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertSoftDeletedCallDataValueParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.not.toBeCompleted()
        myFixture.expect.tables.not.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }

    fun testItCompletesInAssertNotSoftDeletedCallTableParameter() {
        myFixture.configureByFile("integration/tests/completion/inAssertNotSoftDeletedCallTableParameter.php")
        myFixture.completeBasic()

        myFixture.expect.namespaces.toBeCompleted()
        myFixture.expect.tables.toBeCompleted()
        myFixture.expect.columns.not.toBeCompleted()
        myFixture.expect.indexes.not.toBeCompleted()
    }
}
