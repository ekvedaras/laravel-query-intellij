package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasTable
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement

import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

import icons.DatabaseIcons

class ColumnCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return

        if (shouldNotCompleteCurrentParameter(method, parameters)) {
            return
        }

        if (shouldNotCompleteArrayValue(method, parameters)) {
            return
        }

        if (!LaravelUtils.isQueryBuilderMethod(method)) {
            return
        }

        result.addAllElements(
            buildCompletionList(method).distinctBy { it.lookupString }
        )
    }

    private fun shouldNotCompleteCurrentParameter(method: MethodReference, parameters: CompletionParameters) =
        LaravelUtils.BuilderTableColumnsParams[method.name]?.contains(
            MethodUtils.findParameterIndex(parameters.position)
        ) != true

    private fun shouldNotCompleteArrayValue(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderMethodsWithTableColumnsInArrayValues.contains(method.name)
                && parameters.position.parent.parent.elementType?.index?.toInt() == 1889

    private fun buildCompletionList(method: MethodReference): MutableList<LookupElementBuilder> {
        val tablesAndAliases = collectTablesAndAliases(method)
        val completionList = mutableListOf<LookupElementBuilder>()

        DbUtil.getDataSources(method.project).forEach { dataSource ->
            DasUtil.getTables(dataSource.dataSource)
                .filter { !it.isSystem && (tablesAndAliases.isEmpty() || tablesAndAliases.containsValue(it.name)) }
                .forEach { addTableToCompletion(tablesAndAliases, it, completionList) }
        }

        return completionList
    }

    private fun collectTablesAndAliases(method: MethodReference): MutableMap<String, String> {
        val aliases = mutableMapOf<String, String>();

        MethodUtils.findMethodsInTree(method.parentOfType<Statement>()!!.firstChild)
            .filter { LaravelUtils.BuilderTableMethods.contains(it.name) }
            .forEach loop@{
                val tableName = (it.getParameter(0) as StringLiteralExpressionImpl).contents.trim()

                if (tableName.contains(" as ")) {
                    aliases[tableName.substringAfter("as").trim()] = tableName.substringBefore("as").trim()
                    return@loop
                }

                if (!LaravelUtils.BuilderTableAliasParams.containsKey(it.name)) {
                    aliases[tableName] = tableName
                    return@loop
                }

                val aliasParam: Int = LaravelUtils.BuilderTableAliasParams[it.name] ?: return@loop
                val alias: String? = (it.getParameter(aliasParam) as? StringLiteralExpressionImpl)?.contents

                aliases[alias ?: tableName] = tableName
            }

        return aliases
    }

    private fun addTableToCompletion(
        aliases: MutableMap<String, String>,
        table: DasTable,
        completion: MutableList<LookupElementBuilder>
    ) {
        aliases
            .filter { it.value == table.name }
            .forEach { alias ->
                DasUtil.getColumns(table).forEach {
                    completion.add(
                        buildLookup(
                            it,
                            aliases.size > 1,
                            if (alias.key != alias.value) alias.key else null
                        )
                    )
                }
            }
    }

    private fun buildLookup(column: DasColumn, prependTable: Boolean, alias: String? = null): LookupElementBuilder {
        val tableSchema = column.dasParent
            ?: return LookupElementBuilder
                .create(column, column.name)
                .withIcon(DatabaseIcons.Col)

        if (!prependTable) {
            return LookupElementBuilder
                .create(column, column.name)
                .withIcon(DatabaseIcons.Col)
        }

        if (alias != null && alias != tableSchema.name) {
            return LookupElementBuilder
                .create(column, alias + "." + column.name)
                .withIcon(DatabaseIcons.Col)
                .withTailText(" (" + tableSchema.name + ")", true)
                .withTypeText(tableSchema.dasParent?.name, true)
        }

        return LookupElementBuilder
            .create(column, (alias ?: tableSchema.name) + "." + column.name)
            .withIcon(DatabaseIcons.Col)
            .withTypeText(tableSchema.dasParent?.name, true)
    }
}