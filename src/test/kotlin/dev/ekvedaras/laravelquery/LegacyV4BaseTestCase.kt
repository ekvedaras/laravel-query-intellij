package dev.ekvedaras.laravelquery

import com.intellij.database.model.DasDataSource
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import com.intellij.psi.PsiFile

@Suppress("UnnecessaryAbstractClass")
internal abstract class LegacyV4BaseTestCase : BaseTestCase() {
    lateinit var schemas: List<String>
    var schemaTables = mutableMapOf<String, List<String>>()
    lateinit var schemasAndTables: List<String>

    override fun setUp() {
        super.setUp()

        schemas = DasUtil.getSchemas(db).map { schema ->
            schemaTables[schema.name] = schema.getDasChildren(ObjectKind.TABLE).map { it.name }.toList()

            schema.name
        }.toList()

        schemasAndTables = schemaTables.values.flatten() + schemas
    }

    protected fun dataSource(): DasDataSource = db

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
}
