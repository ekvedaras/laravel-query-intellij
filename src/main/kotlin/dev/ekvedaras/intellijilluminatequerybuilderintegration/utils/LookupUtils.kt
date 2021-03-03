package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.DeclarativeInsertHandler
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.psi.DbDataSource
import com.intellij.openapi.project.Project
import com.intellij.sql.symbols.DasPsiWrappingSymbol

class LookupUtils {
    companion object {
        fun DasNamespace.buildLookup(project: Project, dataSource: DbDataSource): LookupElementBuilder =
            LookupElementBuilder
                .create(this, this.name)
                .withIcon(DasPsiWrappingSymbol(this, project).getIcon(false))
                .withTypeText(dataSource.name, true)
                .withInsertHandler(project)

        fun DasTable.buildLookup(project: Project, triggerCompletion: Boolean = false): LookupElementBuilder =
            LookupElementBuilder
                .create(this, this.name)
                .withLookupString("${this.dasParent?.name}.${this.name}")
                .withTypeText(this.dasParent?.name ?: "", true)
                .withIcon(DasPsiWrappingSymbol(this, project).getIcon(false))
                .withInsertHandler(project, triggerCompletion)

        fun DasColumn.buildLookup(project: Project, alias: String? = null): LookupElementBuilder =
            LookupElementBuilder
                .create(this, this.name)
                .withIcon(DasPsiWrappingSymbol(this, project).getIcon(false))
                .withTailText("  ${this.dataType}${if (this.default != null) " = ${this.default}" else ""}", true)
                .withTypeText(this.comment ?: "", true)
                .withLookupString("${alias ?: "${this.table?.dasParent?.name}.${this.tableName}"}.${this.name}")
                .withLookupString("${this.tableName}.${this.name}")
                .withInsertHandler(project)

        fun buildForAlias(
            tableAlias: Map.Entry<String, Pair<String, String?>>,
            dataSource: DbDataSource
        ): LookupElementBuilder =
            LookupElementBuilder
                .create(tableAlias.key)
                .withTailText(if (tableAlias.value.second != null) " (${tableAlias.value.second})" else "", true)
                .withTypeText(dataSource.name, true)
                .withInsertHandler(
                    DeclarativeInsertHandler.Builder()
                        .disableOnCompletionChars(".")
                        .insertOrMove(".")
                        .triggerAutoPopup()
                        .build()
                )

        private fun LookupElementBuilder.withInsertHandler(
            project: Project,
            triggerCompletion: Boolean = false
        ): LookupElementBuilder =
            this.withInsertHandler { context, lookup ->
                context.document.deleteString(context.startOffset, context.tailOffset)
                context.document.insertString(context.startOffset, lookup.lookupString)
                context.editor.caretModel.moveCaretRelatively(
                    lookup.lookupString.length,
                    0,
                    false,
                    false,
                    true
                )

                if (triggerCompletion) {
                    AutoPopupController.getInstance(project).scheduleAutoPopup(context.editor)
                }
            }
    }
}
