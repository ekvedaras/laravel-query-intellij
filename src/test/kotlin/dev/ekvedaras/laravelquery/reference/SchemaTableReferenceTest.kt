package dev.ekvedaras.laravelquery.reference

import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.util.DbUtil
import com.intellij.testFramework.UsefulTestCase
import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import junit.framework.TestCase

internal class SchemaTableReferenceTest : BaseTestCase() {
    fun testResolvesSchemaReference() {
        myFixture.configureByFile("inspection/knownSchema.php")

        val schema = DasUtil.getSchemas(db).first { it.name == "testProject1" }
        val dbSchema = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), schema)
            ?: return fail("Failed to resolve DB schema")

        val usages = myFixture.findUsages(dbSchema)

        UsefulTestCase.assertSize(1, usages)
        TestCase.assertEquals(SchemaPsiReference::class.java, usages.first().referenceClass)
        TestCase.assertTrue(usages.first().element?.textMatches("'testProject1'") ?: false)
        TestCase.assertEquals(55, usages.first().navigationRange.startOffset)
        TestCase.assertEquals(55 + schema.name.length, usages.first().navigationRange.endOffset)
    }

    fun testResolvesTableReference() {
        myFixture.configureByFile("inspection/knownTable.php")

        val table = DasUtil.getTables(dataSource()).first { it.name == "users" }
        val dbTable = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), table)
            ?: return fail("Failed to resolve DB table")

        val usages = myFixture.findUsages(dbTable)

        UsefulTestCase.assertSize(1, usages)
        TestCase.assertEquals(TableOrViewPsiReference::class.java, usages.first().referenceClass)
        TestCase.assertTrue(usages.first().element?.textMatches("'users'") ?: false)
        TestCase.assertEquals(55, usages.first().navigationRange.startOffset)
        TestCase.assertEquals(55 + table.name.length, usages.first().navigationRange.endOffset)
    }

    fun testResolvesTableReferenceWhenPrefixesAreUsed() {
        val settings = LaravelQuerySettings.getInstance(project)
        settings.tablePrefix = "failed_"

        myFixture.configureByFile("inspection/knownWithPrefixTable.php")

        val table = DasUtil.getTables(dataSource()).first { it.name == "failed_jobs" }
        val dbTable = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), table)
            ?: return fail("Failed to resolve DB table")

        val usages = myFixture.findUsages(dbTable)

        UsefulTestCase.assertSize(1, usages)
        TestCase.assertEquals(TableOrViewPsiReference::class.java, usages.first().referenceClass)
        TestCase.assertTrue(usages.first().element?.textMatches("'jobs'") ?: false)
        TestCase.assertEquals(55, usages.first().navigationRange.startOffset)
        TestCase.assertEquals(55 + table.name.length - settings.tablePrefix.length, usages.first().navigationRange.endOffset)

        settings.tablePrefix = ""
    }

    fun testResolvesSchemaAndTableReferences() {
        myFixture.configureByFile("inspection/knownSchemaTable.php")

        val table = DasUtil.getTables(dataSource()).first { it.name == "users" }
        val schema = table.dasParent ?: return fail("Failed to load table schema")

        val dbSchema = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), schema)
            ?: return fail("Failed to resolve DB schema")
        val dbTable = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), table)
            ?: return fail("Failed to resolve DB table")

        val schemaUsages = myFixture.findUsages(dbSchema)
        val tableUsages = myFixture.findUsages(dbTable)

        UsefulTestCase.assertSize(1, schemaUsages)
        UsefulTestCase.assertSize(1, tableUsages)

        TestCase.assertEquals(SchemaPsiReference::class.java, schemaUsages.first().referenceClass)
        TestCase.assertEquals(TableOrViewPsiReference::class.java, tableUsages.first().referenceClass)

        TestCase.assertTrue(schemaUsages.first().element?.textMatches("'testProject1.users'") ?: false)
        TestCase.assertTrue(tableUsages.first().element?.textMatches("'testProject1.users'") ?: false)

        TestCase.assertEquals(55, schemaUsages.first().navigationRange.startOffset)
        TestCase.assertEquals(55 + schema.name.length + 1, tableUsages.first().navigationRange.startOffset)

        TestCase.assertEquals(55 + schema.name.length, schemaUsages.first().navigationRange.endOffset)
        TestCase.assertEquals(
            55 + schema.name.length + 1 + table.name.length,
            tableUsages.first().navigationRange.endOffset
        )
    }
}
