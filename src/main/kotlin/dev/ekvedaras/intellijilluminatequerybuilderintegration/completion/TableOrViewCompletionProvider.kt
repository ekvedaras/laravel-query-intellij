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
import com.intellij.database.psi.DbTableImpl

import com.intellij.database.psi.DbNamespaceImpl

import com.intellij.database.model.DasTable
import icons.DatabaseIcons


class TableOrViewCompletionProvider : CompletionProvider<CompletionParameters>() {
    companion object {
        val BUILDERS: List<String> = listOf(
            "\\Illuminate\\Database\\Query\\Builder",
            "\\Illuminate\\Database\\Eloquent\\Builder",
        )

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

        DbUtil.getDataSources(ref.project).forEach { dataSource ->
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
                " (" + table.dasParent?.name + ")",
                true
            )
        }

        return builder
    }
}