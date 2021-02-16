package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.util.DbUtil
import com.intellij.find.findUsages.FindUsagesHandlerFactory
import com.intellij.refactoring.suggested.startOffset
import com.intellij.testFramework.UsefulTestCase
import com.jetbrains.php.lang.psi.PhpFileImpl
import dev.ekvedaras.intellijilluminatequerybuilderintegration.BaseTestCase
import junit.framework.TestCase

class SchemaTableColumnReferenceTest : BaseTestCase() {
    fun testResolvesColumnReference() {
        myFixture.configureByFile("inspection/knownColumn.php")

        val column = DasUtil.getTables(db).first { it.name == "users" }.getDasChildren(ObjectKind.COLUMN).first { it.name == "id" }
        val dbColumn = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), column) ?: return fail("Failed to resolve DB column")

        val usages = myFixture.findUsages(dbColumn);

        UsefulTestCase.assertSize(1, usages)
        TestCase.assertEquals(ColumnPsiReference::class.java, usages.first().referenceClass)
        TestCase.assertEquals("'id'", usages.first().element?.text)
        TestCase.assertEquals(82, usages.first().navigationRange.startOffset)
        TestCase.assertEquals(82 + column.name.length, usages.first().navigationRange.endOffset)
    }

    fun testResolvesTableAndColumnReference() {
        myFixture.configureByFile("inspection/knownTableColumn.php")

        val table = DasUtil.getTables(db).first { it.name == "users" }
        val column = table.getDasChildren(ObjectKind.COLUMN).first { it.name == "id" }

        val dbTable = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), table) ?: return fail("Failed to resolve DB table")
        val dbColumn = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), column) ?: return fail("Failed to resolve DB column")

        val tableUsages = myFixture.findUsages(dbTable);
        val columnUsages = myFixture.findUsages(dbColumn);

        UsefulTestCase.assertSize(2, tableUsages) // from() + get()
        UsefulTestCase.assertSize(1, columnUsages)

        TestCase.assertEquals(TableOrViewPsiReference::class.java, tableUsages.last().referenceClass)
        TestCase.assertEquals(ColumnPsiReference::class.java, columnUsages.first().referenceClass)

        TestCase.assertEquals("'users.id'", tableUsages.last().element?.text)
        TestCase.assertEquals("'users.id'", columnUsages.first().element?.text)

        TestCase.assertEquals(82, tableUsages.last().navigationRange.startOffset)
        TestCase.assertEquals(82 + table.name.length + 1, columnUsages.first().navigationRange.startOffset)

        TestCase.assertEquals(82 + table.name.length, tableUsages.last().navigationRange.endOffset)
        TestCase.assertEquals(82 + table.name.length + 1 + column.name.length, columnUsages.first().navigationRange.endOffset)
    }

    fun testResolvesSchemaAndTableAndColumnReferences() {
        myFixture.configureByFile("inspection/knownSchemaTableColumn.php")

        val table = DasUtil.getTables(db).first { it.name == "users" }
        val schema = table.dasParent!!
        val column = table.getDasChildren(ObjectKind.COLUMN).first { it.name == "id" }

        val dbSchema = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), schema) ?: return fail("Failed to resolve DB schema")
        val dbTable = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), table) ?: return fail("Failed to resolve DB table")
        val dbColumn = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), column) ?: return fail("Failed to resolve DB column")

        val schemaUsages = myFixture.findUsages(dbSchema);
        val tableUsages = myFixture.findUsages(dbTable);
        val columnUsages = myFixture.findUsages(dbColumn);

        UsefulTestCase.assertSize(2, schemaUsages) // from() + get()
        UsefulTestCase.assertSize(2, tableUsages) // from() + get()
        UsefulTestCase.assertSize(1, columnUsages)

        TestCase.assertEquals(SchemaPsiReference::class.java, schemaUsages.first().referenceClass)
        TestCase.assertEquals(TableOrViewPsiReference::class.java, tableUsages.first().referenceClass)
        TestCase.assertEquals(ColumnPsiReference::class.java, columnUsages.first().referenceClass)

        TestCase.assertEquals("'testProject1.users.id'", schemaUsages.last().element?.text)
        TestCase.assertEquals("'testProject1.users.id'", tableUsages.last().element?.text)
        TestCase.assertEquals("'testProject1.users.id'", columnUsages.first().element?.text)

        TestCase.assertEquals(82, schemaUsages.last().navigationRange.startOffset)
        TestCase.assertEquals(82 + schema.name.length + 1, tableUsages.last().navigationRange.startOffset)
        TestCase.assertEquals(82 + schema.name.length + 1 + table.name.length + 1, columnUsages.first().navigationRange.startOffset)

        TestCase.assertEquals(82 + schema.name.length, schemaUsages.last().navigationRange.endOffset)
        TestCase.assertEquals(82 + schema.name.length + 1 + table.name.length, tableUsages.last().navigationRange.endOffset)
        TestCase.assertEquals(82 + schema.name.length + 1 + table.name.length + 1 + column.name.length, columnUsages.first().navigationRange.endOffset)
    }
}