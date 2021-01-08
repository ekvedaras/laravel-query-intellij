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
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.ClassUtils
import icons.DatabaseIcons


class TableOrViewCompletionProvider : CompletionProvider<CompletionParameters>() {
    companion object {
        @JvmStatic
        val BuilderClasses = listOf(
            "\\Illuminate\\Database\\Query\\Builder",
            "\\Illuminate\\Database\\Eloquent\\Builder",
        )

        @JvmStatic
        val Methods = listOf(
            "from",
            "join",
            "joinWhere",
            "leftJoin",
            "leftJoinWhere",
            "rightJoin",
            "rightJoinWhere",
            "crossJoin",
        )

        @JvmStatic
        val Aliases = hashMapOf(
            "from" to 1,
            "fromSub" to 1,
            "selectSub" to 1,
        )
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = ClassUtils.resolveMethodReference(parameters.position) ?: return
        if (!Methods.contains(method.name) || ClassUtils.findParameterIndex(parameters.position) != 0) {
            return
        }

        val classes: List<String> = ClassUtils.resolveMethodClasses(method)
        if (BuilderClasses.none { classes.contains(it) }) {
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