package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.DeclarativeInsertHandler
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils
import java.util.*

class TableOrViewCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return
        val project = method.project

        if (shouldNotCompleteCurrentParam(method, parameters)) {
            return
        }

        if (!LaravelUtils.isQueryBuilderMethod(method, project)) {
            return
        }

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Table)

        if (target.parts.size == 1) {
            val addedSchemas = mutableListOf<String>()
            val syncAddedSchemas = Collections.synchronizedList(addedSchemas)
            val addedTables = mutableListOf<String>()
            val syncAddedTables = Collections.synchronizedList(addedTables)

            DbUtil.getDataSources(project).toList().parallelStream().forEach { dataSource ->
                DasUtil.getSchemas(dataSource).toList().parallelStream()
                    .filter { !addedSchemas.contains(it.name) }
                    .forEach {
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

                        syncAddedSchemas.add(it.name)
                    }

                DasUtil.getTables(dataSource)
                    .toList().parallelStream()
                    .filter { !it.isSystem && !addedTables.contains(it.name) }
                    .forEach {
                        result.addElement(
                            LookupElementBuilder
                                .create(it, it.name)
                                .withIcon(DasPsiWrappingSymbol(it, project).getIcon(false))
                                .withTailText(" (" + it.dasParent?.name + ")", true)
                                .withTypeText(dataSource.name, true)
                                .withInsertHandler(
                                    DeclarativeInsertHandler.Builder().build()
                                )
                        )

                        syncAddedTables.add(it.name)
                    }
            }
        } else if (target.parts.size == 2) {
            val addedTables = mutableListOf<String>()
            val syncAddedTables = Collections.synchronizedList(addedTables)

            target.schema.parallelStream().forEach { schema ->
                schema.getDasChildren(ObjectKind.TABLE)
                    .toList().parallelStream()
                    .filter {
                        !(it as DasTable).isSystem && target.schema.contains(it.dasParent) && !addedTables.contains(
                            it.name
                        )
                    }
                    .forEach {
                        val lookup = it.dasParent?.name + "." + it.name
                        result.addElement(
                            LookupElementBuilder
                                .create(it, it.name)
                                .withLookupString(lookup)
                                .withTypeText(schema.name)
                                .withIcon(DasPsiWrappingSymbol(it, project).getIcon(false))
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

                        syncAddedTables.add(it.name)
                    }
            }
        }
    }

    private fun shouldNotCompleteCurrentParam(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderTableMethods.contains(method.name)
                || MethodUtils.findParameterIndex(parameters.position) != 0
                || (parameters.position.parent?.parent?.parent is FunctionReference && parameters.position.parent?.parent?.parent !is MethodReference)
}