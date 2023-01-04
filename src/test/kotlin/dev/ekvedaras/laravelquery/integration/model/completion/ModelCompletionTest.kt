package dev.ekvedaras.laravelquery.integration.model.completion

import dev.ekvedaras.laravelquery.BaseTestCase

internal class ModelCompletionTest : BaseTestCase() {
    fun testItCompletesColumnsInCreateCallArrayHashKey() {
        myFixture.configureByFile("integration/model/completion/inCreateCallArrayHashKey.php")
        myFixture.completeBasic()
        assertCompletion("email", "first_name")
        assertNoCompletion("billable_id")
        assertNoCompletion("testProject1", "testProject2", "users", "customers", "failed_jobs", "migrations")
    }

    fun testItCompletesColumnsInCreateCallArrayEntry() {
        myFixture.configureByFile("integration/model/completion/inCreateCallArrayEntry.php")
        myFixture.completeBasic()
        assertCompletion("email", "first_name")
        assertNoCompletion("billable_id")
        assertNoCompletion("testProject1", "testProject2", "users", "customers", "failed_jobs", "migrations")
    }

    fun testItDoesNotCompleteColumnsInCreateCallArrayHashValue() {
        myFixture.configureByFile("integration/model/completion/inCreateCallArrayHashValue.php")
        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElements ?: arrayOf())
    }

    fun testItDoesNotCompleteColumnsInNonModelClasses() {
        myFixture.configureByFile("integration/model/completion/inCreateCallOfNonModelClass.php")
        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElements ?: arrayOf())
    }
}
