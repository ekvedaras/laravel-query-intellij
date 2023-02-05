package dev.ekvedaras.laravelquery

import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.database.Dbms
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.dataSource.LocalDataSourceManager
import com.intellij.psi.PsiFile
import com.intellij.sql.database.SqlCommonTestUtils
import com.intellij.testFramework.TestDataFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

internal abstract class BaseTestCase : BasePlatformTestCase() {
    override fun getTestDataPath(): String = "src/test/resources"
    protected lateinit var db: LocalDataSource

    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("stubs.php")

        LaravelQuerySettings.getInstance(project).filterDataSources = false

        db = SqlCommonTestUtils.createDataSourceFromSql(
            project,
            Dbms.MYSQL,
            false,
            File("./src/test/resources/test-db.sql").readText(Charsets.UTF_8)
        )
    }

    override fun tearDown() {
        LaravelQuerySettings.getInstance(project).tablePrefix = ""
        LocalDataSourceManager.getInstance(project).removeDataSource(db)

        super.tearDown()
    }

    protected fun useTablePrefix(prefix: String): String {
        LaravelQuerySettings.getInstance(project).tablePrefix = prefix
        return prefix
    }

    companion object {
        fun assertLookupContains(vararg strings: String, inFixture: CodeInsightTestFixture) {
            val suggestedStrings = inFixture.lookupElementStrings ?: return fail("Empty completion result")

            assertContainsElements(suggestedStrings, strings.asList())
        }

        fun assertLookupDoesNotContain(vararg strings: String, inFixture: CodeInsightTestFixture) {
            val suggestedStrings = inFixture.lookupElementStrings ?: return

            assertDoesntContain(suggestedStrings, strings.asList())
        }

        fun assertLookup(contains: Boolean, vararg strings: String, inFixture: CodeInsightTestFixture) {
            if (contains) assertLookupContains(*strings, inFixture = inFixture)
            else assertLookupDoesNotContain(*strings, inFixture = inFixture)
        }
    }

    protected fun assertCompletion(vararg shouldContain: String) =
        assertLookupContains(*shouldContain, inFixture = myFixture)

    protected fun assertNoCompletion(vararg shouldNotContain: String) =
        assertLookupDoesNotContain(*shouldNotContain, inFixture = myFixture)

    protected fun assertInspection(@TestDataFile filePath: String, inspection: InspectionProfileEntry) {
        myFixture.enableInspections(inspection)

        // Delay is required otherwise tests randomly fail due to PSI tree changes during highlighting 🤷‍
        runBlocking { delay(500L) }

        myFixture.testHighlighting(filePath)
    }

    protected fun assertInspection(file: PsiFile, inspection: InspectionProfileEntry) {
        myFixture.enableInspections(inspection)

        // Delay is required otherwise tests randomly fail due to PSI tree changes during highlighting 🤷‍
        runBlocking { delay(500L) }

        myFixture.testHighlighting(true, false, false, file.virtualFile)
    }
}
