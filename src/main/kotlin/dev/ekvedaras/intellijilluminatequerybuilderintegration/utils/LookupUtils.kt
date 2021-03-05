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

class LookupUtils private constructor() {
    companion object {
        fun DasNamespace.buildLookup(project: Project, dataSource: DbDataSource): LookupElementBuilder =
            LookupElementBuilder
                .create(this, this.name)
                .withIcon(DasPsiWrappingSymbol(this, project).getIcon(false))
                .withTypeText(dataSource.name, true)
                .withInsertHandler(project, true)

        fun DasTable.buildLookup(
            project: Project,
            withTablePrefix: Boolean = false,
            triggerCompletion: Boolean = false
        ): LookupElementBuilder =
            LookupElementBuilder
                .create(this, this.name)
                .withLookupString("${this.dasParent?.name}.${this.name}")
                .withTypeText(this.dasParent?.name ?: "", true)
                .withIcon(DasPsiWrappingSymbol(this, project).getIcon(false))
                .withInsertHandler(
                    project,
                    triggerCompletion,
                    if (withTablePrefix) {
                        this.dasParent?.name ?: ""
                    } else {
                        ""
                    }
                )

        fun DasColumn.buildLookup(
            project: Project,
            withTablePrefix: Boolean = false,
            withSchemaPrefix: Boolean = false,
            alias: String? = null
        ): LookupElementBuilder {
            val prefix = if (withSchemaPrefix) {
                this.table?.dasParent?.name ?: ""
            } else {
                ""
            } + "." + if (withTablePrefix) {
                this.dasParent?.name ?: ""
            } else {
                ""
            }

            return LookupElementBuilder
                .create(this, this.name)
                .withIcon(DasPsiWrappingSymbol(this, project).getIcon(false))
                .withTailText("  ${this.dataType}${if (this.default != null) " = ${this.default}" else ""}", true)
                .withTypeText(this.comment ?: "", true)
                .withLookupString("${alias ?: "${this.table?.dasParent?.name}.${this.tableName}"}.${this.name}")
                .withLookupString("${this.tableName}.${this.name}")
                .withInsertHandler(
                    project,
                    false,
                    alias ?: prefix.trim('.')
                )
        }

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
            triggerCompletion: Boolean = false,
            prefix: String = ""
        ): LookupElementBuilder {
            var lookupPrefix = prefix
            if (prefix.isNotEmpty()) {
                lookupPrefix += "."
            }

            val suffix = if (triggerCompletion) {
                "."
            } else {
                ""
            }

            return this.withInsertHandler { context, lookup ->
                context.document.deleteString(context.startOffset, context.tailOffset)
                context.document.insertString(
                    context.startOffset, "${lookupPrefix}${lookup.lookupString}$suffix"
                )
                context.editor.caretModel.moveCaretRelatively(
                    lookupPrefix.length + lookup.lookupString.length + suffix.length,
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
}
