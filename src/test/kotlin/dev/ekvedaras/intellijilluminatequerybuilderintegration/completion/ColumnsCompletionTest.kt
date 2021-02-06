package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import dev.ekvedaras.intellijilluminatequerybuilderintegration.BaseTestCase
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import kotlin.test.expect

class ColumnsCompletionTest : BaseTestCase() {
    private fun caretAfterArgs(at: Int, prefix: String = ""): String {
        var args = ""

        for (arg in 0 until at) {
            args += "'',"
        }

        args += "'$prefix<caret>'"

        return args
    }

    private fun completeFor(from: String, prefix: String, method: String, argument: Int) {
        myFixture.configureByText(
            "test.php",
            run {
                val args = caretAfterArgs(argument, prefix)
                "<?php (new Illuminate\\Database\\Query\\Builder())->from('$from')->$method($args)"
            }
        )
        myFixture.completeBasic()
    }

    fun testCompletesSchemasAndTables() {
        val table =
            DasUtil.getTables(db).filter { !it.isSystem }.firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table)

        val expected = columns.map { it.name } + listOf(table.name)
        val size = columns.size() + 1

        LaravelUtils.BuilderTableColumnsParams.forEach { method, params ->
            params.forEach params@{ param ->
                completeFor(table.name, "", method, param)

                assertEquals(size, myFixture.lookupElementStrings?.size)  // TODO not working for method latest
                assertCompletion(*expected.toList().toTypedArray())
                myFixture.lookup.hideLookup(true)
            }
        }
    }
}