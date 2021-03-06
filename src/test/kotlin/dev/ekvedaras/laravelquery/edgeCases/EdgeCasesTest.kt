package dev.ekvedaras.laravelquery.edgeCases

import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.util.DbUtil
import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.inspection.UnknownColumnInspection
import dev.ekvedaras.laravelquery.inspection.UnknownTableOrViewInspection
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@Suppress("Deprecation")
internal class EdgeCasesTest : BaseTestCase() {
    fun testClassCastException1() {
        myFixture.configureByFile("edgeCases/classCastException1.php")
        myFixture.completeBasic()
        assertCompletion("email")
    }

    fun testClassCastException2() {
        myFixture.configureByFile("edgeCases/classCastException2.php")
        runBlocking { delay(500L) }
        myFixture.completeBasic()
        assertCompletion("email")
    }

    fun testNonQueryBuilderTableMethod() {
        val file = myFixture.configureByFile("edgeCases/nonQueryBuilderTableMethod.php")
        val schema = DasUtil.getSchemas(db).first()
        val dbSchema = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), schema)
            ?: return fail("Failed to resolve DB schema")

        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElementStrings?.toList() ?: listOf<String>())
        assertEmpty(myFixture.findUsages(dbSchema))
        assertInspection(file!!, UnknownTableOrViewInspection())
    }

    fun testNonQueryBuilderColumnMethod() {
        val file = myFixture.configureByFile("edgeCases/nonQueryBuilderColumnMethod.php")
        val schema = DasUtil.getSchemas(db).first()
        val dbSchema = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), schema)
            ?: return fail("Failed to resolve DB schema")

        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElementStrings?.toList() ?: listOf<String>())
        assertEmpty(myFixture.findUsages(dbSchema))
        assertInspection(file!!, UnknownColumnInspection())
    }

    fun testDoesNotResolvesColumnReferenceIfStringContainsDollarSign() {
        myFixture.configureByFile("edgeCases/nonCompletableArrayValue.php")
        myFixture.completeBasic()
        assertEmpty(myFixture.lookupElementStrings?.toList() ?: listOf<String>())
    }
}