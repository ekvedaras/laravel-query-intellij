package dev.ekvedaras.intellijilluminatequerybuilderintegration.edgeCases

import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.util.DbUtil
import com.intellij.testFramework.UsefulTestCase
import dev.ekvedaras.intellijilluminatequerybuilderintegration.BaseTestCase
import dev.ekvedaras.intellijilluminatequerybuilderintegration.inspection.UnknownColumnInspection
import dev.ekvedaras.intellijilluminatequerybuilderintegration.inspection.UnknownTableOrViewInspection
import dev.ekvedaras.intellijilluminatequerybuilderintegration.reference.ColumnPsiReference
import junit.framework.TestCase

class EdgeCasesTest : BaseTestCase() {
    fun testClassCastException1() {
        myFixture.configureByFile("edgeCases/classCastException1.php")
        myFixture.completeBasic()
        assertCompletion("email")
    }

    fun testClassCastException2() {
        myFixture.configureByFile("edgeCases/classCastException2.php")
        myFixture.completeBasic()
        assertCompletion("email")
    }

    fun testNonQueryBuilderTableMethod() {
        val file = myFixture.configureByFile("edgeCases/nonQueryBuilderTableMethod.php")
        val schema = DasUtil.getSchemas(db).first()
        val dbSchema = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), schema) ?: return fail("Failed to resolve DB schema")

        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElementStrings?.toList() ?: listOf<String>())
        assertEmpty(myFixture.findUsages(dbSchema))
        assertInspection(file!!, UnknownTableOrViewInspection());
    }

    fun testNonQueryBuilderColumnMethod() {
        val file = myFixture.configureByFile("edgeCases/nonQueryBuilderColumnMethod.php")
        val schema = DasUtil.getSchemas(db).first()
        val dbSchema = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), schema) ?: return fail("Failed to resolve DB schema")

        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElementStrings?.toList() ?: listOf<String>())
        assertEmpty(myFixture.findUsages(dbSchema))
        assertInspection(file!!, UnknownColumnInspection());
    }

    fun testDoesNotResolvesColumnReferenceIfStringContainsDollarSign() {
        myFixture.configureByFile("edgeCases/nonCompletableArrayValue.php")
        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElementStrings?.toList() ?: listOf<String>())
    }
}