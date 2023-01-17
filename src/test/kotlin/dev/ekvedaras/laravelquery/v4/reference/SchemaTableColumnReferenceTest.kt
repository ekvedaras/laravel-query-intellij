package dev.ekvedaras.laravelquery.v4.reference

import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.util.DbUtil
import com.intellij.testFramework.UsefulTestCase
import dev.ekvedaras.laravelquery.LegacyV4BaseTestCase
import junit.framework.TestCase

internal class SchemaTableColumnReferenceTest : LegacyV4BaseTestCase() {
    fun testResolvesColumnReference() {
        myFixture.configureByFile("v4/inspection/knownColumn.php")

        val column = DasUtil.getTables(dataSource())
            .first { it.name == "users" }
            .getDasChildren(ObjectKind.COLUMN)
            .first { it.name == "id" }
        val dbColumn = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), column)
            ?: return fail("Failed to resolve DB column")

        val usages = myFixture.findUsages(dbColumn)

        UsefulTestCase.assertSize(1, usages)
        TestCase.assertEquals(ColumnPsiReference::class.java, usages.first().referenceClass)
        TestCase.assertTrue(usages.first().element?.textMatches("'id'") ?: false)
        TestCase.assertEquals(82, usages.first().navigationRange.startOffset)
        TestCase.assertEquals(82 + column.name.length, usages.first().navigationRange.endOffset)
    }

    fun testItDoesNotResolveColumnsFromOtherTablesBecauseOfTheContext() {
        myFixture.configureByFile("v4/inspection/knownColumn.php")

        val columnFromOtherTable = DasUtil.getTables(dataSource())
            .first { it.name == "customers" }
            .getDasChildren(ObjectKind.COLUMN)
            .first { it.name == "id" }
        val dbColumnFromOtherTable = DbImplUtil.findElement(
            DbUtil.getDataSources(project).first(),
            columnFromOtherTable
        ) ?: return fail("Failed to resolve DB column from other table")

        val otherUsages = myFixture.findUsages(dbColumnFromOtherTable)

        UsefulTestCase.assertSize(0, otherUsages)
    }

    fun testResolvesTableAndColumnReference() {
        myFixture.configureByFile("v4/inspection/knownTableColumn.php")

        val table = DasUtil.getTables(dataSource()).first { it.name == "users" }
        val column = table.getDasChildren(ObjectKind.COLUMN).first { it.name == "id" }

        val dbTable = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), table)
            ?: return fail("Failed to resolve DB table")
        val dbColumn = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), column)
            ?: return fail("Failed to resolve DB column")

        val tableUsages = myFixture.findUsages(dbTable)
        val columnUsages = myFixture.findUsages(dbColumn)

        UsefulTestCase.assertSize(2, tableUsages) // from() + get()
        UsefulTestCase.assertSize(1, columnUsages)

        TestCase.assertEquals(TableOrViewPsiReference::class.java, tableUsages.last().referenceClass)
        TestCase.assertEquals(ColumnPsiReference::class.java, columnUsages.first().referenceClass)

        TestCase.assertTrue(tableUsages.last().element?.textMatches("'users.id'") ?: false)
        TestCase.assertTrue(columnUsages.first().element?.textMatches("'users.id'") ?: false)

        TestCase.assertEquals(82, tableUsages.last().navigationRange.startOffset)
        TestCase.assertEquals(82 + table.name.length + 1, columnUsages.first().navigationRange.startOffset)

        TestCase.assertEquals(82 + table.name.length, tableUsages.last().navigationRange.endOffset)
        TestCase.assertEquals(
            82 + table.name.length + 1 + column.name.length,
            columnUsages.first().navigationRange.endOffset
        )
    }

    @Suppress("ReturnCount")
    fun testResolvesSchemaAndTableAndColumnReferences() {
        myFixture.configureByFile("v4/inspection/knownSchemaTableColumn.php")

        val table = DasUtil.getTables(dataSource()).first { it.name == "users" }
        val schema = table.dasParent ?: return fail("Failed to load table schema")
        val column = table.getDasChildren(ObjectKind.COLUMN).first { it.name == "id" }

        val dbSchema = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), schema)
            ?: return fail("Failed to resolve DB schema")
        val dbTable = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), table)
            ?: return fail("Failed to resolve DB table")
        val dbColumn = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), column)
            ?: return fail("Failed to resolve DB column")

        val schemaUsages = myFixture.findUsages(dbSchema)
        val tableUsages = myFixture.findUsages(dbTable)
        val columnUsages = myFixture.findUsages(dbColumn)

        UsefulTestCase.assertSize(2, schemaUsages) // from() + get()
        UsefulTestCase.assertSize(2, tableUsages) // from() + get()
        UsefulTestCase.assertSize(1, columnUsages)

        TestCase.assertEquals(SchemaPsiReference::class.java, schemaUsages.first().referenceClass)
        TestCase.assertEquals(TableOrViewPsiReference::class.java, tableUsages.first().referenceClass)
        TestCase.assertEquals(ColumnPsiReference::class.java, columnUsages.first().referenceClass)

        TestCase.assertTrue(schemaUsages.last().element?.textMatches("'testProject1.users.id'") ?: false)
        TestCase.assertTrue(tableUsages.last().element?.textMatches("'testProject1.users.id'") ?: false)
        TestCase.assertTrue(columnUsages.first().element?.textMatches("'testProject1.users.id'") ?: false)

        TestCase.assertEquals(82, schemaUsages.last().navigationRange.startOffset)
        TestCase.assertEquals(82 + schema.name.length + 1, tableUsages.last().navigationRange.startOffset)
        TestCase.assertEquals(
            82 + schema.name.length + 1 + table.name.length + 1,
            columnUsages.first().navigationRange.startOffset
        )

        TestCase.assertEquals(82 + schema.name.length, schemaUsages.last().navigationRange.endOffset)
        TestCase.assertEquals(
            82 + schema.name.length + 1 + table.name.length,
            tableUsages.last().navigationRange.endOffset
        )
        TestCase.assertEquals(
            82 + schema.name.length + 1 + table.name.length + 1 + column.name.length,
            columnUsages.first().navigationRange.endOffset
        )
    }

    fun testResolvesJsonColumnReference() {
        myFixture.configureByFile("v4/inspection/knownJsonColumn.php")

        val column = DasUtil.getTables(dataSource())
            .first { it.name == "users" }
            .getDasChildren(ObjectKind.COLUMN)
            .first { it.name == "id" }
        val dbColumn = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), column)
            ?: return fail("Failed to resolve DB column")

        val usages = myFixture.findUsages(dbColumn)

        UsefulTestCase.assertSize(1, usages)
        TestCase.assertEquals(ColumnPsiReference::class.java, usages.first().referenceClass)
        TestCase.assertTrue(usages.first().element?.textMatches("'id->prop'") ?: false)
        TestCase.assertEquals(82, usages.first().navigationRange.startOffset)
        TestCase.assertEquals(82 + column.name.length, usages.first().navigationRange.endOffset)
    }
}
