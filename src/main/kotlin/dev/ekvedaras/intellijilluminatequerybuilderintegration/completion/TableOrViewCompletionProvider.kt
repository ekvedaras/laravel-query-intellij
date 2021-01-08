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
        if (!LaravelUtils.BuilderTableMethods.contains(method.name) || MethodUtils.findParameterIndex(parameters.position) != 0) {
            return
        }

        val classes: List<String> = MethodUtils.resolveMethodClasses(method)
        if (LaravelUtils.DatabaseBuilderClasses.none { classes.contains(it) }) {
            return
        }

        DbUtil.getDataSources(method.project).forEach { dataSource ->
            DasUtil.getTables(dataSource.dataSource).forEach {
                if (!it.isSystem) {
                    result.addElement(buildLookup(it))
                }
            }
        }
    }

    private fun buildLookup(table: DasTable): LookupElementBuilder {
        var builder = LookupElementBuilder.create(table, table.name).withIcon(DatabaseIcons.Table)

        val tableSchema = table.dasParent
        if (tableSchema != null) {
            if (tableSchema is DbNamespaceImpl) {
                builder = builder.withTypeText(
                    tableSchema.parent?.name,
                    true
                )
            }
        }

        if (tableSchema != null) {
            builder = builder.withTailText(
                " (" + tableSchema.name + ")",
                true
            )
        }

        return builder
    }
}