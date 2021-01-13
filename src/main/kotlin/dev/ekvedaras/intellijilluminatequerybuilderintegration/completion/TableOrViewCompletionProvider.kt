package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.DeclarativeInsertHandler
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.symbols.DasPsiWrappingSymbol
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class TableOrViewCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return

        if (shouldNotCompleteCurrentParam(method, parameters)) {
            return
        }

        if (!LaravelUtils.isQueryBuilderMethod(method)) {
            return
        }

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Table)

        if (target.parts.size == 1) {
            DbUtil.getDataSources(method.project).forEach { dataSource ->
                result.addAllElements(
                    DasUtil.getSchemas(dataSource).map {
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

                result.addAllElements(
                    DasUtil.getTables(dataSource)
                        .filter { !it.isSystem }
                        .map {
                            LookupElementBuilder
                                .create(it, it.name)
                                .withIcon(DasPsiWrappingSymbol(it, method.project).getIcon(false))
                                .withTailText(" (" + it.dasParent?.name + ")", true)
                                .withTypeText(dataSource.name, true)
                                .withInsertHandler(
                                    DeclarativeInsertHandler.Builder().build()
                                )
                        }
                )
            }
        } else if (target.parts.size == 2) {
            target.schema.forEach { schema ->
                result.addAllElements(
                    schema.getDasChildren(ObjectKind.TABLE)
                        .filter { !(it as DasTable).isSystem && target.schema.contains(it.dasParent) }
                        .map {
                            val lookup = it.dasParent?.name + "." + it.name
                            LookupElementBuilder
                                .create(it, it.name)
                                .withLookupString(lookup)
                                .withIcon(DasPsiWrappingSymbol(it, method.project).getIcon(false))
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

    private fun shouldNotCompleteCurrentParam(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderTableMethods.contains(method.name)
                || MethodUtils.findParameterIndex(parameters.position) != 0
}