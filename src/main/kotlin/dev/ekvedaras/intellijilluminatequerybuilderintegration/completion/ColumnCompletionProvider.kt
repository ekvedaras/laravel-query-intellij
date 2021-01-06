package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference

import com.intellij.database.psi.DbNamespaceImpl
import com.intellij.database.psi.DbTableImpl
import com.intellij.sql.slicer.toSqlElement
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import com.jetbrains.rd.util.addUnique
import com.jetbrains.rd.util.lifetime.Lifetime
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.ClassUtils

import icons.DatabaseIcons


class ColumnCompletionProvider : CompletionProvider<CompletionParameters>() {
    companion object {
        @JvmStatic
        val BUILDERS: List<String> = listOf(
            "\\Illuminate\\Database\\Query\\Builder",
            "\\Illuminate\\Database\\Eloquent\\Builder",
        )

        @JvmStatic
        val METHODS: List<String> = listOf(
            "where",
        )
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = ClassUtils.resolveMethodReference(parameters.position) ?: return
        if (!METHODS.contains(method.name)) {
            return
        }

        val classes: List<String> = ClassUtils.resolveMethodClasses(method)
        if (BUILDERS.none { classes.contains(it) }) {
            return
        }

        val tableNames = mutableListOf<String>();
        val treeMethods = ClassUtils.findMethodsInTree(method)
        for (treeMethod in treeMethods) {
            if (TableOrViewCompletionProvider.METHODS.contains(treeMethod.name)) {
                tableNames.addUnique(
                    Lifetime.Eternal,
                    (treeMethod.getParameter(0) as StringLiteralExpressionImpl).contents
                )
            }
        }

        val completion = mutableListOf<LookupElementBuilder>()
        DbUtil.getDataSources(method.project).forEach { dataSource ->
            DasUtil.getTables(dataSource.dataSource)
                .forEach { table ->
                    if (!table.isSystem && (tableNames.isEmpty() || tableNames.contains(table.name))) {
                        DasUtil.getColumns(table)
                            .forEach {
                                completion.add(buildLookup(it, tableNames.size > 1))
                            }
                    }
                }
        }

        result.addAllElements(completion.distinctBy { it.lookupString })
    }

    private fun buildLookup(column: DasColumn, prependTable : Boolean): LookupElementBuilder {
        val tableSchema = column.dasParent
            ?: return LookupElementBuilder.create(column, column.name).withIcon(DatabaseIcons.Col)

        if (!prependTable) { // TODO there should probably be a setting to always force table prepend
            return LookupElementBuilder.create(column, column.name).withIcon(DatabaseIcons.Col)
        }

        return LookupElementBuilder.create(column, tableSchema.name + "." + column.name)
            .withIcon(DatabaseIcons.Col)
            .withTypeText(tableSchema.dasParent?.name, true)
    }
}