package dev.ekvedaras.laravelquery.utils

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.DeclarativeInsertHandler
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasObject
import com.intellij.database.model.DasTable
import com.intellij.database.psi.DbDataSource
import com.intellij.openapi.project.Project
import com.intellij.sql.symbols.DasPsiWrappingSymbol

class LookupUtils private constructor() {
    companion object {
        fun DasNamespace.buildLookup(project: Project, dataSource: DbDataSource): LookupElement =
            PrioritizedLookupElement.withGrouping(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder
                        .create(this, this.name)
                        .withIcon(this.getIcon(project))
                        .withTypeText(dataSource.name, true)
                        .withInsertHandler(project, true),
                    1.0
                ), 1
            )

        fun DasTable.buildLookup(
            project: Project,
            withTablePrefix: Boolean = false,
            triggerCompletion: Boolean = false
        ): LookupElement =
            PrioritizedLookupElement.withGrouping(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder
                        .create(this, this.name)
                        .withLookupString("${this.dasParent?.name}.${this.name}")
                        .withTypeText(this.dasParent?.name ?: "", true)
                        .withIcon(this.getIcon(project))
                        .withInsertHandler(
                            project,
                            triggerCompletion,
                            if (withTablePrefix) {
                                this.dasParent?.name ?: ""
                            } else {
                                ""
                            }
                        ),
                    2.0
                ),
                2
            )

        fun DasColumn.buildLookup(
            project: Project,
            withTablePrefix: Boolean = false,
            withSchemaPrefix: Boolean = false,
            alias: String? = null
        ): LookupElement {
            val prefix = if (withSchemaPrefix) {
                this.table?.dasParent?.name ?: ""
            } else {
                ""
            } + "." + if (withTablePrefix) {
                this.dasParent?.name ?: ""
            } else {
                ""
            }

            return PrioritizedLookupElement.withGrouping(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder
                        .create(this, this.name)
                        .withIcon(this.getIcon(project))
                        .withTailText(
                            "  ${this.dataType}${if (this.default != null) " = ${this.default}" else ""}",
                            true
                        )
                        .withTypeText("${this.comment ?: ""} ${this.tableName}", true)
                        .withLookupString("${alias ?: "${this.table?.dasParent?.name}.${this.tableName}"}.${this.name}")
                        .withLookupString("${this.tableName}.${this.name}")
                        .withInsertHandler(
                            project,
                            false,
                            alias ?: prefix.trim('.')
                        ),
                    4.0
                ),
                4
            )
        }

        fun buildForAliasOrTable(
            tableAlias: Map.Entry<String, Pair<String, String?>>,
            dataSource: DbDataSource
        ): LookupElement =
            PrioritizedLookupElement.withGrouping(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder
                        .create(tableAlias.key)
                        .withTailText(
                            if (tableAlias.value.second != null) "  (${tableAlias.value.second})" else "",
                            true
                        )
                        .withTypeText(dataSource.name, true)
                        .withInsertHandler(
                            DeclarativeInsertHandler.Builder()
                                .disableOnCompletionChars(".")
                                .insertOrMove(".")
                                .triggerAutoPopup()
                                .build()
                        ),
                    3.0
                ),
                3
            )

        fun DasObject.getIcon(project: Project) =
            DasPsiWrappingSymbol(this, project).getIcon(false)

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
