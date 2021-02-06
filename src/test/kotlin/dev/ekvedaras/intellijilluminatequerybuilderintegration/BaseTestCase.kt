package dev.ekvedaras.intellijilluminatequerybuilderintegration

import com.intellij.database.Dbms
import com.intellij.sql.database.SqlCommonTestUtils
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

abstract class BaseTestCase : BasePlatformTestCase() {
    override fun getTestDataPath(): String = "src/test/resources"

    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("stubs.php")

        SqlCommonTestUtils.createDataSourceFromSql(
            project,
            Dbms.MYSQL,
            false,
            File("./src/test/resources/test-db.sql").readText(Charsets.UTF_8)
        )
    }

    protected fun assertCompletion(vararg shouldContain: String) {
        myFixture.completeBasic()

        val strings = myFixture.lookupElementStrings ?: return fail("empty completion result")

        assertContainsElements(strings, shouldContain.asList())
    }
}