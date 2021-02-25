package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasTable
import com.intellij.openapi.project.Project
import com.intellij.sql.symbols.DasPsiWrappingSymbol

class LookupUtils {
    companion object {
        fun forTable(project: Project, table: DasTable, triggerCompletion: Boolean = false): LookupElementBuilder {
            val builder = LookupElementBuilder
                .create(table, table.name)
                .withLookupString("${table.dasParent?.name}.${table.name}")
                .withTypeText(table.dasParent?.name)
                .withIcon(DasPsiWrappingSymbol(table, project).getIcon(false))

            return if (triggerCompletion) withCompletionTrigger(builder) else builder
        }

        private fun withCompletionTrigger(builder: LookupElementBuilder): LookupElementBuilder =
            builder.withInsertHandler { context, lookup ->
                context.document.deleteString(context.startOffset, context.tailOffset)
                context.document.insertString(context.startOffset, lookup.lookupString)
                context.editor.caretModel.moveCaretRelatively(
                    lookup.lookupString.length,
                    0,
                    false,
                    false,
                    true
                )
            }
    }
}