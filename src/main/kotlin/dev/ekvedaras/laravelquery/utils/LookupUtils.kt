package dev.ekvedaras.laravelquery.utils

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasForeignKey
import com.intellij.database.model.DasIndex
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasObject
import com.intellij.database.model.DasTable
import com.intellij.database.model.DasTableKey
import com.intellij.database.psi.DbDataSource
import com.intellij.openapi.project.Project
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import dev.ekvedaras.laravelquery.completion.DeclarativeInsertHandler
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.nameWithoutPrefix
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.tableNameWithoutPrefix
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.withoutTablePrefix
import icons.DatabaseIcons

class LookupUtils private constructor() {
    companion object {
        private const val NamespacePriority = 1
        private const val TablePriority = 2
        private const val AliasPriority = 3
        private const val ColumnPriority = 4

        fun DasNamespace.buildLookup(project: Project, dataSource: DbDataSource): LookupElement =
            PrioritizedLookupElement.withGrouping(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder
                        .create(this, this.name)
                        .withIcon(this.getIcon(project))
                        .withTypeText(dataSource.name, true)
                        .withInsertHandler(project, true),
                    NamespacePriority.toDouble()
                ),
                NamespacePriority
            )

        fun DasTable.buildLookup(
            project: Project,
            withTablePrefix: Boolean = false,
            triggerCompletion: Boolean = false
        ): LookupElement =
            PrioritizedLookupElement.withGrouping(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder
                        .create(this, this.nameWithoutPrefix(project))
                        .withLookupString("${this.dasParent?.name}.${this.nameWithoutPrefix(project)}")
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
                    TablePriority.toDouble()
                ),
                TablePriority
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
                this.tableNameWithoutPrefix(project)
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
                        .withTypeText("${this.comment ?: ""} ${this.tableNameWithoutPrefix(project)}", true)
                        .withLookupString("${alias ?: "${this.table?.dasParent?.name}.${this.tableNameWithoutPrefix(project)}"}.${this.name}")
                        .withLookupString("${this.tableNameWithoutPrefix(project)}.${this.name}")
                        .withInsertHandler(
                            project,
                            false,
                            alias ?: prefix.trim('.')
                        ),
                    ColumnPriority.toDouble()
                ),
                ColumnPriority
            )
        }

        fun DasIndex.buildLookup(project: Project): LookupElement {
            return LookupElementBuilder
                .create(this, this.name)
                .withIcon(this.getIcon(project))
                .withTypeText(
                    "  ${if (this.isUnique) " unique" else ""} ${if (this.isFunctionBased) " function" else ""}",
                    true
                )
                .withTailText("${this.comment ?: ""} ${this.columnsRef.names().joinToString(", ")}", true)
                .withInsertHandler(
                    project,
                    false,
                )
        }

        fun DasTableKey.buildLookup(project: Project): LookupElement {
            return LookupElementBuilder
                .create(this, this.name)
                .withIcon(this.getIcon(project))
                .withTypeText(
                    "  ${if (this.isPrimary) " primary" else ""}",
                    true
                )
                .withTailText("${this.comment ?: ""} ${this.columnsRef.names().joinToString(", ")}", true)
                .withInsertHandler(
                    project,
                    false,
                )
        }

        fun DasForeignKey.buildLookup(project: Project): LookupElement {
            return LookupElementBuilder
                .create(this, this.name)
                .withIcon(this.getIcon(project))
                .withTypeText(
                    "  ${this.refTableName.withoutTablePrefix(project)}: ${this.columnsRef.names().joinToString(", ")}",
                    true
                )
                .withTailText(this.comment ?: "", true)
                .withInsertHandler(
                    project,
                    false,
                )
        }

        fun buildForAliasOrTable(
            project: Project,
            tableAlias: Map.Entry<String, Pair<String, String?>>,
            dataSource: DbDataSource,
            table: DasTable?
        ): LookupElement =
            PrioritizedLookupElement.withGrouping(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder
                        .create(tableAlias.key)
                        .withIcon(table?.getIcon(project) ?: DatabaseIcons.Synonym)
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
                    AliasPriority.toDouble()
                ),
                AliasPriority
            )

        private fun DasObject.getIcon(project: Project) =
            DasPsiWrappingSymbol(this, project).getIcon(false)

        fun LookupElementBuilder.withInsertHandler(
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
