package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.util.ProcessingContext

import com.intellij.database.psi.DbNamespaceImpl

import com.intellij.database.model.DasTable
import com.intellij.database.symbols.DasPsiWrappingSymbol
import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils
import icons.DatabaseIcons

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

        DbUtil.getDataSources(method.project).forEach { dataSource ->
            DasUtil.getTables(dataSource.dataSource)
                .filter { !it.isSystem }
                .forEach { result.addElement(buildLookup(it, method.project)) }
        }
    }

    private fun shouldNotCompleteCurrentParam(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderTableMethods.contains(method.name)
                || MethodUtils.findParameterIndex(parameters.position) != 0

    private fun buildLookup(table: DasTable, project: Project): LookupElementBuilder {
        var builder = LookupElementBuilder
            .create(table, table.name)
            .withIcon(DasPsiWrappingSymbol(table, project).getIcon(false))

        val tableSchema = table.dasParent ?: return builder

        if (tableSchema is DbNamespaceImpl) {
            builder = builder.withTypeText(tableSchema.parent?.name, true)
        }

        return builder.withTailText(" (" + tableSchema.name + ")", true)
    }
}