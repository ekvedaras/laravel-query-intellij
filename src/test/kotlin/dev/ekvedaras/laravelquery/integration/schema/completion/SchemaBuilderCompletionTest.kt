package dev.ekvedaras.laravelquery.integration.schema.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables

internal class SchemaBuilderCompletionTest : BaseTestCase() {
    fun testItCompletesInCreateMethodCall() {
        myFixture.configureByFile("integration/schema/completion/inCreateCallOfAnonymousMigration.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.expect(myFixture).toBeCompleted()
        Columns.expect(myFixture).not().toBeCompleted()
    }
}
