package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference

import com.intellij.database.psi.DbNamespaceImpl
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import com.jetbrains.rd.util.addUnique
import com.jetbrains.rd.util.lifetime.Lifetime

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
        val identifier: PsiElement = parameters.position.parent?.parent?.prevSibling?.prevSibling ?: return

        var found = false
        for (method in METHODS) {
            if (identifier.text == method) {
                found = true
                break
            }
        }

        if (!found) {
            return
        }

        val ref: MethodReference = identifier.prevSibling?.prevSibling as MethodReference? ?: return
        val types: Set<String> =
            PhpIndex
                .getInstance(ref.project)
                .completeType(ref.project, ref.inferredType, null)
                .types

        found = false
        for (builder in BUILDERS) {
            if (types.contains(builder)) {
                found = true
                break
            }
        }

        if (!found) {
            return
        }

        // TODO should traverse nested and not just loop
        val tableNames = mutableListOf<String>();
        for (child in identifier.parent.children) {
            if(child is MethodReference && TableOrViewCompletionProvider.METHODS.contains(child.name)) {
                tableNames.addUnique(Lifetime.Eternal, (child.getParameter(0) as StringLiteralExpressionImpl).contents)
            }
        }

        DbUtil.getDataSources(ref.project).forEach { dataSource ->
            DasUtil.getTables(dataSource.dataSource)
                .forEach { table ->
                    if (!table.isSystem && (tableNames.isEmpty() || tableNames.contains(table.name))) {
                        DasUtil.getColumns(table)
                            .forEach {
                                result.addElement(buildLookup(it))
                            }
                    }
                }
        }
    }

    private fun buildLookup(column: DasColumn): LookupElementBuilder {
        var builder = LookupElementBuilder.create(column, column.name).withIcon(DatabaseIcons.Col)

        val tableSchema = column.dasParent
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
                " (" + column.dasParent?.name + ")",
                true
            )
        }

        return builder
    }
}