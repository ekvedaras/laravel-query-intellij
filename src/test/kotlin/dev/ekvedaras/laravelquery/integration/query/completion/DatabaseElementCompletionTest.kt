package dev.ekvedaras.laravelquery.integration.query.completion

import dev.ekvedaras.laravelquery.BaseTestCase

internal class DatabaseElementCompletionTest : BaseTestCase() {
    fun testItCompletesNamespaces() {
        myFixture.configureByFile("integration/query/completion/inFromCall.php")
        myFixture.completeBasic()
        assertCompletion("testProject1", "testProject2")
    }

    fun testItCompletesTables() {
        myFixture.configureByFile("integration/query/completion/inFromCall.php")
        myFixture.completeBasic()
        assertCompletion("users", "customers")
    }
}
