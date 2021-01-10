package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.AutoPopupControllerImpl
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasTable
import com.intellij.database.symbols.DasPsiWrappingSymbol
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
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

        var schema: String? = null
        if (parameters.position.text.contains(".")) {
            schema = parameters.position.text.substringBefore(".")
        }

        DbUtil.getDataSources(method.project).forEach { dataSource ->
            if (schema == null) {
                DasUtil.getSchemas(dataSource).forEach {
                    result.addElement(
                        LookupElementBuilder
                            .create(it, it.name + ".")
                            .withIcon(DasPsiWrappingSymbol(it, method.project).getIcon(false))
                            .withInsertHandler { _, _ ->
                                AutoPopupControllerImpl.getInstance(method.project).scheduleAutoPopup(parameters.editor)
                            }
                    )
                }
            }

            DasUtil.getTables(dataSource.dataSource)
                .filter {
                    !it.isSystem && (schema == null || it.dasParent?.name == schema)
                }
                .forEach { result.addElement(buildLookup(it, schema != null, method.project)) }
        }
    }

    private fun shouldNotCompleteCurrentParam(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderTableMethods.contains(method.name)
                || MethodUtils.findParameterIndex(parameters.position) != 0

    private fun buildLookup(table: DasTable, prependSchema: Boolean, project: Project): LookupElementBuilder {
        val tableSchema = table.dasParent ?: return LookupElementBuilder
            .create(table.name)
            .withIcon(DasPsiWrappingSymbol(table, project).getIcon(false))

        if (prependSchema) {
            return LookupElementBuilder
                .create(table, tableSchema.name + "." + table.name)
                .withIcon(DasPsiWrappingSymbol(table, project).getIcon(false))
        }

        return LookupElementBuilder
            .create(table.name)
            .withIcon(DasPsiWrappingSymbol(table, project).getIcon(false))
            .withTailText(" (" + tableSchema.name + ")", true)
            .withLookupString(tableSchema.name + "." + table.name)
    }
}