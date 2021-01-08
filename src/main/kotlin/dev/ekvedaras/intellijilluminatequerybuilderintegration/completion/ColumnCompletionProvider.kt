package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
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

        val classes: List<String> = MethodUtils.resolveMethodClasses(method)
        if (LaravelUtils.DatabaseBuilderClasses.none { classes.contains(it) }) {
            return
        }

        val aliases = mutableMapOf<String, String>();
        val treeMethods = MethodUtils.findMethodsInTree(method.parentOfType<Statement>()!!.firstChild)
        for (treeMethod in treeMethods) {
            if (LaravelUtils.BuilderTableMethods.contains(treeMethod.name)) {
                val tableName = (treeMethod.getParameter(0) as StringLiteralExpressionImpl).contents.trim()

                if (tableName.contains(" as ")) {
                    aliases[tableName.substringAfter("as").trim()] = tableName.substringBefore("as").trim()
                    continue
                }

                if (!LaravelUtils.BuilderTableAliasParams.containsKey(treeMethod.name)) {
                    aliases[tableName] = tableName
                    continue
                }

                val aliasParam: Int = LaravelUtils.BuilderTableAliasParams[treeMethod.name] ?: continue
                val alias: String? =
                    (treeMethod.getParameter(aliasParam) as? StringLiteralExpressionImpl)?.contents

                aliases[alias ?: tableName] = tableName
            }
        }

        val completion = mutableListOf<LookupElementBuilder>()
        DbUtil.getDataSources(method.project).forEach { dataSource ->
            DasUtil.getTables(dataSource.dataSource).forEach { table ->
                if (!table.isSystem && (aliases.isEmpty() || aliases.containsValue(table.name))) {
                    aliases.filter {
                        it.value == table.name
                    }.forEach { alias ->
                        if (alias.key == alias.value) {
                            DasUtil.getColumns(table).forEach {
                                completion.add(buildLookup(it, aliases.size > 1))
                            }
                        } else {
                            DasUtil.getColumns(table).forEach {
                                completion.add(buildLookup(it, aliases.size > 1, alias.key))
                            }
                        }
                    }
                }
            }
        }

        result.addAllElements(completion.distinctBy { it.lookupString })
    }

    private fun shouldNotCompleteCurrentParameter(
        method: MethodReference,
        parameters: CompletionParameters
    ) =
        LaravelUtils.BuilderTableColumnsParams[method.name]?.contains(MethodUtils.findParameterIndex(parameters.position)) != true

    private fun shouldNotCompleteArrayValue(
        method: MethodReference,
        parameters: CompletionParameters
    ) = (!LaravelUtils.BuilderMethodsWithTableColumnsInArrayValues.contains(method.name)
            && parameters.position.parent.parent.elementType?.index?.toInt() == 1889)

    private fun buildLookup(column: DasColumn, prependTable: Boolean, alias: String? = null): LookupElementBuilder {
        val tableSchema = column.dasParent
            ?: return LookupElementBuilder.create(column, column.name).withIcon(DatabaseIcons.Col)

        if (!prependTable) { // TODO there should probably be a setting to always force table prepend
            return LookupElementBuilder.create(column, column.name).withIcon(DatabaseIcons.Col)
        }

        if (alias != null && alias != tableSchema.name) {
            return LookupElementBuilder.create(column, alias + "." + column.name)
                .withIcon(DatabaseIcons.Col)
                .withTailText(" (" + tableSchema.name + ")", true)
                .withTypeText(tableSchema.dasParent?.name, true)
        }

        return LookupElementBuilder.create(column, (alias ?: tableSchema.name) + "." + column.name)
            .withIcon(DatabaseIcons.Col)
            .withTypeText(tableSchema.dasParent?.name, true)
    }
}