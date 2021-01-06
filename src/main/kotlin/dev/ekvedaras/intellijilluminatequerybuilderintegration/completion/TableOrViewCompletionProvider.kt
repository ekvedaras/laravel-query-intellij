package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference

import com.intellij.database.psi.DbNamespaceImpl

import com.intellij.database.model.DasTable
import com.intellij.sql.slicer.toSqlElement
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.ClassUtils
import icons.DatabaseIcons


class TableOrViewCompletionProvider : CompletionProvider<CompletionParameters>() {
    companion object {
        @JvmStatic
        val BUILDERS: List<String> = listOf(
            "\\Illuminate\\Database\\Query\\Builder",
            "\\Illuminate\\Database\\Eloquent\\Builder",
        )

        @JvmStatic
        val METHODS: List<String> = listOf(
            "from",
            "join",
            "joinWhere",
            "leftJoin",
            "leftJoinWhere",
            "rightJoin",
            "rightJoinWhere",
            "crossJoin",
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

        DbUtil.getDataSources(method.project).forEach { dataSource ->
            DasUtil.getTables(dataSource.dataSource)
                .forEach {
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