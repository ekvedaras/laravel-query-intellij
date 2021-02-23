package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.DeclarativeInsertHandler
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.util.elementType
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class ColumnCompletionProvider(private val completeFullList: Boolean = false) :
    CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return
        val project = method.project

        if (shouldNotCompleteCurrentParameter(method, parameters)) {
            return
        }

        if (shouldNotCompleteArrayValue(method, parameters)) {
            return
        }

        if (!LaravelUtils.isQueryBuilderMethod(method, project)) {
            return
        }


        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Column)

        if (target.parts.size == 1) {
            val schemas = target.tablesAndAliases.map { it.value.second }.filterNotNull().distinct()
            DbUtil.getDataSources(project).toList().parallelStream().forEach { dataSource ->
                DasUtil.getSchemas(dataSource)
                    .toList().parallelStream()
                    .filter { completeFullList || schemas.isEmpty() || schemas.contains(it.name) }
                    .forEach {
                        if (target.tablesAndAliases.isEmpty() || completeFullList) {
                            it.getDasChildren(ObjectKind.TABLE)
                                .toList().parallelStream()
                                .forEach { dasTable ->
                                    result.addElement(
                                        LookupElementBuilder
                                            .create(dasTable.name)
                                            .withTailText(" (" + it.name + ")", true)
                                            .withTypeText(dataSource.name, true)
                                            .withIcon(
                                                DasPsiWrappingSymbol(dasTable, project).getIcon(false)
                                            )
                                            .withInsertHandler(
                                                DeclarativeInsertHandler.Builder()
                                                    .disableOnCompletionChars(".")
                                                    .insertOrMove(".")
                                                    .triggerAutoPopup()
                                                    .build()
                                            )
                                    )
                                }
                        }

                        result.addElement(
                            LookupElementBuilder
                                .create(it, it.name)
                                .withIcon(DasPsiWrappingSymbol(it, project).getIcon(false))
                                .withTypeText(dataSource.name, true)
                                .withInsertHandler(
                                    DeclarativeInsertHandler.Builder()
                                        .disableOnCompletionChars(".")
                                        .insertOrMove(".")
                                        .triggerAutoPopup()
                                        .build()
                                )
                        )
                    }

                if (target.tablesAndAliases.isNotEmpty()) {
                    result.addLookupAdvertisement("CTRL(CMD) + SHIFT + Space to see all options")
                    target.tablesAndAliases.forEach {
                        var lookup = LookupElementBuilder
                            .create(it.key)
                            .withTailText(if (it.value.second != null) " (" + it.value.second + ")" else "", true)
                            .withTypeText(dataSource.name, true)
                            .withInsertHandler(
                                DeclarativeInsertHandler.Builder()
                                    .disableOnCompletionChars(".")
                                    .insertOrMove(".")
                                    .triggerAutoPopup()
                                    .build()
                            )

                        val table = DasUtil.getTables(dataSource).find { table ->
                            table.name == it.value.first && (it.value.second == null || table.dasParent?.name == it.value.second)
                        }

                        if (table != null) {
                            lookup = lookup.withIcon(
                                DasPsiWrappingSymbol(table, project).getIcon(false)
                            )

                            table.getDasChildren(ObjectKind.COLUMN)
                                .toList().parallelStream()
                                .forEach { column ->
                                    result.addElement(
                                        LookupElementBuilder
                                            .create(column, column.name)
                                            .withIcon(DasPsiWrappingSymbol(column, project).getIcon(false))
                                            .withTypeText(table.name)
                                            .withInsertHandler(
                                                DeclarativeInsertHandler.Builder().build()
                                            )
                                    )
                                }
                        }

                        result.addElement(lookup)
                    }
                }
            }
        } else if (target.parts.size == 2) {
            DbUtil.getDataSources(project).toList().parallelStream().forEach { dataSource ->
                if (target.schema.isNotEmpty()) {
                    target.schema.parallelStream().forEach { schema ->
                        schema.getDasChildren(ObjectKind.TABLE)
                            .toList().parallelStream()
                            .filter { !(it as DasTable).isSystem }
                            .forEach {
                                val lookup = target.parts.first() + "." + it.name
                                result.addElement(
                                    LookupElementBuilder
                                        .create(it, it.name)
                                        .withIcon(DasPsiWrappingSymbol(it, project).getIcon(false))
                                        .withTailText(" (" + it.dasParent?.name + ")", true)
                                        .withTypeText(dataSource.name, true)
                                        .withLookupString(lookup)
                                        .withInsertHandler { context, _ ->
                                            context.document.deleteString(context.startOffset, context.tailOffset)
                                            context.document.insertString(context.startOffset, "$lookup.")
                                            context.editor.caretModel.moveCaretRelatively(
                                                lookup.length + 1,
                                                0,
                                                false,
                                                false,
                                                true
                                            )
                                            AutoPopupController.getInstance(project)
                                                .scheduleAutoPopup(context.editor)
                                        }
                                )
                            }
                    }
                } else {
                    target.table.parallelStream().forEach { table ->
                        table.getDasChildren(ObjectKind.COLUMN)
                            .toList().parallelStream()
                            .forEach {
                                val lookup = target.parts.first() + "." + it.name
                                result.addElement(
                                    LookupElementBuilder
                                        .create(it, it.name)
                                        .withIcon(DasPsiWrappingSymbol(it, project).getIcon(false))
                                        .withLookupString(lookup)
                                        .withInsertHandler { context, _ ->
                                            context.document.deleteString(context.startOffset, context.tailOffset)
                                            context.document.insertString(context.startOffset, lookup)
                                            context.editor.caretModel.moveCaretRelatively(
                                                lookup.length,
                                                0,
                                                false,
                                                false,
                                                true
                                            )
                                        }
                                )
                            }
                    }
                }
            }
        } else if (target.parts.size == 3) {
            target.table.parallelStream().forEach { table ->
                table.getDasChildren(ObjectKind.COLUMN)
                    .toList().parallelStream()
                    .forEach {
                        val lookup = target.parts.first() + "." + target.parts[1] + "." + it.name
                        result.addElement(
                            LookupElementBuilder
                                .create(it, it.name)
                                .withIcon(DasPsiWrappingSymbol(it, project).getIcon(false))
                                .withLookupString(lookup)
                                .withInsertHandler { context, _ ->
                                    context.document.deleteString(context.startOffset, context.tailOffset)
                                    context.document.insertString(context.startOffset, lookup)
                                    context.editor.caretModel.moveCaretRelatively(
                                        lookup.length,
                                        0,
                                        false,
                                        false,
                                        true
                                    )
                                }
                        )
                    }
            }
        }
    }

    private fun shouldNotCompleteCurrentParameter(method: MethodReference, parameters: CompletionParameters) =
        parameters.position.textContains('$')
                || !LaravelUtils.BuilderTableColumnsParams.containsKey(method.name)
                || (!LaravelUtils.BuilderTableColumnsParams[method.name]!!.contains(
            MethodUtils.findParameterIndex(
                parameters.position
            )
        )
                && !LaravelUtils.BuilderTableColumnsParams[method.name]!!.contains(-1))
                || (parameters.position.parent?.parent?.parent is FunctionReference && parameters.position.parent?.parent?.parent !is MethodReference)

    private fun shouldNotCompleteArrayValue(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderMethodsWithTableColumnsInArrayValues.contains(method.name)
                && (parameters.position.parent.parent.elementType?.index?.toInt() == 1889 // 1889 - array expression
                || parameters.position.parent.parent.elementType?.index?.toInt() == 805) // 805 - array value
}