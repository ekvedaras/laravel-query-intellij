package dev.ekvedaras.laravelquery

import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.database.Dbms
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.dataSource.LocalDataSourceManager
import com.intellij.database.model.DasDataSource
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import com.intellij.psi.PsiFile
import com.intellij.sql.database.SqlCommonTestUtils
import com.intellij.testFramework.TestDataFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

@Suppress("UnnecessaryAbstractClass", "Deprecation")
internal abstract class BaseTestCase : BasePlatformTestCase() {
    override fun getTestDataPath(): String = "src/test/resources"
    lateinit var db: LocalDataSource
    lateinit var schemas: List<String>
    var schemaTables = mutableMapOf<String, List<String>>()
    lateinit var schemasAndTables: List<String>

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

        schemas = DasUtil.getSchemas(db).map { schema ->
            schemaTables[schema.name] = schema.getDasChildren(ObjectKind.TABLE).map { it.name }.toList()

            schema.name
        }.toList()

        schemasAndTables = schemaTables.values.flatten() + schemas
    }

    override fun tearDown() {
        LaravelQuerySettings.getInstance(project).tablePrefix = ""
        LocalDataSourceManager.getInstance(project).removeDataSource(db)

        super.tearDown()
    }

    protected fun dataSource(): DasDataSource = db

    protected fun useTablePrefix(prefix: String): String {
        LaravelQuerySettings.getInstance(project).tablePrefix = prefix
        return prefix
    }

    private fun caretAfterArgs(at: Int, prefix: String = ""): String {
        var args = ""

        repeat((0 until at).count()) { args += "''," }

        args += "'$prefix<caret>'"

        return args
    }

    protected fun configureQueryBuilderMethod(from: String, prefix: String, method: String, argument: Int): PsiFile? {
        return myFixture.configureByText(
            "test.php",
            run {
                val args = caretAfterArgs(argument, prefix)
                "<?php (new Illuminate\\Database\\Query\\Builder())->from('$from')->$method($args);"
            }
        )
    }

    protected fun assertCompletion(vararg shouldContain: String) {
        val strings = myFixture.lookupElementStrings ?: return fail("Empty completion result")

        assertContainsElements(strings, shouldContain.asList())
    }

    protected fun assertNoCompletion(vararg shouldNotContain: String) {
        val strings = myFixture.lookupElementStrings ?: return

        assertDoesntContain(strings, shouldNotContain.asList())
    }

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
