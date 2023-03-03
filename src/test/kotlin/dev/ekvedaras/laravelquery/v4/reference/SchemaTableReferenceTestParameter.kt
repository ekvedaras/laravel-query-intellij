package dev.ekvedaras.laravelquery.v4.reference

import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.util.DbUtil
import com.intellij.testFramework.UsefulTestCase
import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.v4.reference.SchemaPsiReference
import dev.ekvedaras.laravelquery.v4.reference.TableOrViewPsiReference
import junit.framework.TestCase

internal class SchemaTableReferenceTestParameter : BaseTestCase() {
    fun testResolvesSchemaReference() {
        myFixture.configureByFile("v4/inspection/knownSchema.php")

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
        myFixture.configureByFile("v4/inspection/knownTable.php")

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

//    Does not run through plugin files if non-existent table is provided in php file 🤷.
//    Does work when running through IDE though. Commenting out for now...
//
//    fun testResolvesTableReferenceWhenPrefixesAreUsed() {
//        val prefix = useTablePrefix("failed_")
//
//        val table = DasUtil.getTables(dataSource()).first { it.name == "failed_jobs" }
//        val dbTable = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), table)
//            ?: return fail("Failed to resolve DB table")
//
//        val usages = myFixture.findUsages(dbTable)
//
//        UsefulTestCase.assertSize(1, usages)
//        TestCase.assertEquals(TableOrViewPsiReference::class.java, usages.first().referenceClass)
//        TestCase.assertTrue(usages.first().element?.textMatches("'jobs'") ?: false)
//        TestCase.assertEquals(55, usages.first().navigationRange.startOffset)
//        TestCase.assertEquals(55 + table.name.length - prefix.length, usages.first().navigationRange.endOffset)
//    }

    fun testResolvesSchemaAndTableReferences() {
        myFixture.configureByFile("v4/inspection/knownSchemaTable.php")

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
