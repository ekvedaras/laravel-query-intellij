package dev.ekvedaras.laravelquery.schema

import dev.ekvedaras.laravelquery.BaseTestCase
import junit.framework.TestCase

internal class SchemaCompletionTest : BaseTestCase() {
    fun testCompletesTables() {
        myFixture.configureByFile("schema/table.php")
        myFixture.completeBasic()

        assertCompletion(*schemaTables.values.flatten().toTypedArray())
        assertNoCompletion(*schemas.toTypedArray())
    }
}
