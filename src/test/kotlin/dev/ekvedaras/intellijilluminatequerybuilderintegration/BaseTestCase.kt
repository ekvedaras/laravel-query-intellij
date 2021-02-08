package dev.ekvedaras.intellijilluminatequerybuilderintegration

import com.intellij.database.Dbms
import com.intellij.database.dataSource.LocalDataSource
import com.intellij.database.dataSource.LocalDataSourceManager
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import com.intellij.sql.database.SqlCommonTestUtils
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

abstract class BaseTestCase : BasePlatformTestCase() {
    override fun getTestDataPath(): String = "src/test/resources"
    lateinit var db: LocalDataSource
    lateinit var schemas: List<String>
    var schemaTables = mutableMapOf<String, List<String>>()
    lateinit var schemasAndTables: List<String>

    override fun setUp() {
        super.setUp()

        myFixture.copyFileToProject("stubs.php")

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
        LocalDataSourceManager.getInstance(project).removeDataSource(db)

        super.tearDown()
    }

    protected fun assertCompletion(vararg shouldContain: String) {
        val strings = myFixture.lookupElementStrings ?: return fail("Empty completion result")

        assertContainsElements(strings, shouldContain.asList())
    }

    protected fun assertNoCompletion(vararg shouldNotContain: String) {
        val strings = myFixture.lookupElementStrings ?: return

        assertDoesntContain(strings, shouldNotContain.asList())
    }
}