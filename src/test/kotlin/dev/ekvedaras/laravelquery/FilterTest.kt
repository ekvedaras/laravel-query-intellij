package dev.ekvedaras.laravelquery

import dev.ekvedaras.laravelquery.v4.models.SettingsSchema
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings

internal class FilterTest : BaseTestCase() {
    private fun complete(method: String, caretPrefix: String = "", methodPrefix: String = "") {
        myFixture.configureByText(
            "test.php",
            "<?php (new Illuminate\\Database\\Query\\Builder())->$methodPrefix$method('$caretPrefix<caret>')"
        )
        myFixture.completeBasic()
    }

    @SuppressWarnings("UnsafeCallOnNullableType")
    fun testCompletesOnlyFilteredSchemasAndTables() {
        LaravelQuerySettings.getInstance(project).filterDataSources = true
        LaravelQuerySettings.getInstance(project).filteredDataSources = setOf(
            SettingsSchema.keyFor("testProject1", dataSource().uniqueId)
        )

        complete("from")

        assertEquals(schemaTables["testProject1"]!!.size + 1, myFixture.lookupElementStrings?.size)
        assertCompletion(*(schemaTables["testProject1"]!! + "testProject1").toTypedArray())
    }

    fun testCompletesAllSchemasAndTablesWhenFilteringIsDisabled() {
        LaravelQuerySettings.getInstance(project).filterDataSources = false
        LaravelQuerySettings.getInstance(project).filteredDataSources = setOf(
            SettingsSchema.keyFor("testProject1", dataSource().uniqueId)
        )

        complete("from")

        assertEquals(schemasAndTables.size, myFixture.lookupElementStrings?.size)
        assertCompletion(*schemasAndTables.toTypedArray())
    }

    fun testCompletesOnlyFilteredColumns() {
        LaravelQuerySettings.getInstance(project).filterDataSources = true
        LaravelQuerySettings.getInstance(project).filteredDataSources = setOf(
            SettingsSchema.keyFor("testProject1", dataSource().uniqueId)
        )

        complete("where", "failed_jobs.", "from('testProject2.failed_jobs')->")
        assertEquals(0, myFixture.lookupElementStrings?.size)

        complete("where", "failed_jobs.", "from('failed_jobs')->")
        assertEquals(0, myFixture.lookupElementStrings?.size)

        complete("where", "", "from('failed_jobs')->")
        assertEquals(2, myFixture.lookupElementStrings?.size)
        // filtered schema + reference table name
        assertCompletion(*listOf("testProject1", "failed_jobs").toTypedArray())
    }
}
