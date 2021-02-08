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

class ColumnCompletionProvider(private val completeFullList: Boolean = false) : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return

        if (shouldNotCompleteCurrentParameter(method, parameters)) {
            return
        }

        if (shouldNotCompleteArrayValue(method, parameters)) {
            return
        }

        if (!LaravelUtils.isQueryBuilderMethod(method)) {
            return
        }


        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Column)

        if (target.parts.size == 1) {
            val schemas = target.tablesAndAliases.map { it.value.second }.filterNotNull().distinct()
            DbUtil.getDataSources(method.project).forEach { dataSource ->
                result.addAllElements(
                    DasUtil.getSchemas(dataSource)
                        .filter { completeFullList || schemas.isEmpty() || schemas.contains(it.name) }
                        .map {
                            if (target.tablesAndAliases.isEmpty() || completeFullList) {
                                result.addAllElements(
                                    it.getDasChildren(ObjectKind.TABLE).map { dasTable ->
                                        LookupElementBuilder
                                            .create(dasTable.name)
                                            .withTailText(" (" + it.name + ")", true)
                                            .withTypeText(dataSource.name, true)
                                            .withIcon(
                                                DasPsiWrappingSymbol(dasTable, method.project).getIcon(false)
                                            )
                                            .withInsertHandler(
                                                DeclarativeInsertHandler.Builder()
                                                    .disableOnCompletionChars(".")
                                                    .insertOrMove(".")
                                                    .triggerAutoPopup()
                                                    .build()
                                            )
                                    }
                                )
                            }

                            LookupElementBuilder
                                .create(it, it.name)
                                .withIcon(DasPsiWrappingSymbol(it, method.project).getIcon(false))
                                .withTypeText(dataSource.name, true)
                                .withInsertHandler(
                                    DeclarativeInsertHandler.Builder()
                                        .disableOnCompletionChars(".")
                                        .insertOrMove(".")
                                        .triggerAutoPopup()
                                        .build()
                                )
                        }
                )

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

                        val table = DasUtil.getTables(dataSource)
                            .find { table ->
                                table.name == it.value.first && (it.value.second == null || table.dasParent?.name == it.value.second)
                            }

                        if (table != null) {
                            lookup = lookup.withIcon(
                                DasPsiWrappingSymbol(table, method.project).getIcon(false)
                            )

                            result.addAllElements(
                                table.getDasChildren(ObjectKind.COLUMN)
                                    .map { column ->
                                        LookupElementBuilder
                                            .create(column, column.name)
                                            .withIcon(DasPsiWrappingSymbol(column, method.project).getIcon(false))
                                            .withTypeText(table.name)
                                            .withInsertHandler(
                                                DeclarativeInsertHandler.Builder().build()
                                            )
                                    }
                            )
                        }

                        result.addElement(lookup)
                    }
                }
            }
        } else if (target.parts.size == 2) {
            DbUtil.getDataSources(method.project).forEach { dataSource ->
                if (target.schema.isNotEmpty()) {
                    target.schema.forEach { schema ->
                        result.addAllElements(
                            schema.getDasChildren(ObjectKind.TABLE)
                                .filter { !(it as DasTable).isSystem }
                                .map {
                                    val lookup = target.parts.first() + "." + it.name
                                    LookupElementBuilder
                                        .create(it, it.name)
                                        .withIcon(DasPsiWrappingSymbol(it, method.project).getIcon(false))
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
                                            AutoPopupController.getInstance(method.project)
                                                .scheduleAutoPopup(context.editor)
                                        }
                                }
                        )
                    }
                } else {
                    target.table.forEach { table ->
                        result.addAllElements(
                            table.getDasChildren(ObjectKind.COLUMN)
                                .map {
                                    val lookup = target.parts.first() + "." + it.name
                                    LookupElementBuilder
                                        .create(it, it.name)
                                        .withIcon(DasPsiWrappingSymbol(it, method.project).getIcon(false))
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
                                }
                        )
                    }
                }
            }
        } else if (target.parts.size == 3) {
            target.table.forEach { table ->
                result.addAllElements(
                    table.getDasChildren(ObjectKind.COLUMN)
                        .map {
                            val lookup = target.parts.first() + "." + target.parts[1] + "." + it.name
                            LookupElementBuilder
                                .create(it, it.name)
                                .withIcon(DasPsiWrappingSymbol(it, method.project).getIcon(false))
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
                        }
                )
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
                && parameters.position.parent.parent.elementType?.index?.toInt() == 1889
}