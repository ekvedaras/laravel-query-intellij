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
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl

import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import com.jetbrains.rd.util.addUnique
import com.jetbrains.rd.util.lifetime.Lifetime
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.ClassUtils

import icons.DatabaseIcons
import org.jetbrains.debugger.values.ArrayValue


class ColumnCompletionProvider : CompletionProvider<CompletionParameters>() {
    companion object {
        @JvmStatic
        val Methods = mapOf(
            "select" to listOf(1),
            "addSelect" to listOf(1),
            "join" to listOf(1, 2, 3),
            "joinWhere" to listOf(1),
            "joinSub" to listOf(2, 3, 4),
            "leftJoin" to listOf(1, 2, 3),
            "leftJoinWhere" to listOf(1),
            "leftJoinSub" to listOf(2, 3, 4),
            "rightJoin" to listOf(1, 2, 3),
            "rightJoinWhere" to listOf(1),
            "rightJoinSub" to listOf(2, 3, 4),
            "crossJoin" to listOf(1, 2, 3),
            "where" to listOf(0),
            "orWhere" to listOf(0),
            "whereColumn" to listOf(0, 1, 2),
            "orWhereColumn" to listOf(0, 1, 2),
            "whereIn" to listOf(0),
            "orWhereIn" to listOf(0),
            "whereNotIn" to listOf(0),
            "orWhereNotIn" to listOf(0),
            "whereIntegerInRaw" to listOf(0),
            "orWhereIntegerInRaw" to listOf(0),
            "orWhereIntegerNotInRaw" to listOf(0),
            "whereNull" to listOf(0),
            "orWhereNull" to listOf(0),
            "whereNotNull" to listOf(0),
            "whereBetween" to listOf(0),
            "whereBetweenColumns" to listOf(0, 1),
            "orWhereBetween" to listOf(0),
            "orWhereBetweenColumns" to listOf(0, 1),
            "whereNotBetween" to listOf(0),
            "whereNotBetweenColumns" to listOf(0, 1),
            "orWhereNotBetween" to listOf(0),
            "orWhereNotBetweenColumns" to listOf(0, 1),
            "orWhereNotNull" to listOf(0),
            "whereDate" to listOf(0),
            "orWhereDate" to listOf(0),
            "whereTime" to listOf(0),
            "orWhereTime" to listOf(0),
            "whereDay" to listOf(0),
            "orWhereDay" to listOf(0),
            "whereMonth" to listOf(0),
            "orWhereMonth" to listOf(0),
            "whereYear" to listOf(0),
            "orWhereYear" to listOf(0),
            "whereRowValues" to listOf(0),
            "orWhereRowValues" to listOf(0),
            "whereJsonContains" to listOf(0),
            "orWhereJsonContains" to listOf(0),
            "whereJsonDoesntContain" to listOf(0),
            "orWhereJsonDoesntContain" to listOf(0),
            "whereJsonLength" to listOf(0),
            "orWhereJsonLength" to listOf(0),
            "groupBy" to listOf(0..50),
            "having" to listOf(0),
            "orHaving" to listOf(0),
            "havingBetween" to listOf(0),
            "orderBy" to listOf(0),
            "orderByDesc" to listOf(0),
            "latest" to listOf(0),
            "oldest" to listOf(0),
            "forPageBeforeId" to listOf(2),
            "forPageAfterId" to listOf(2),
            "reorder" to listOf(0),
            "find" to listOf(1),
            "value" to listOf(0),
            "get" to listOf(0),
            "paginate" to listOf(1),
            "simplePaginate" to listOf(1),
            "getCountForPagination" to listOf(0),
            "pluck" to listOf(0, 1),
            "implode" to listOf(0),
            "count" to listOf(0),
            "min" to listOf(0),
            "max" to listOf(0),
            "sum" to listOf(0),
            "avg" to listOf(0),
            "average" to listOf(0),
            "aggregate" to listOf(1),
            "numericAggregate" to listOf(1),
            "insertUsing" to listOf(1),
            "insertUsing" to listOf(1),
            "increment" to listOf(0),
            "decrement" to listOf(0),
            "updateOrInsert" to listOf(0, 1),
            "update" to listOf(0),
        )

        @JvmStatic
        val CompleteArrayValuesFor = listOf(
            "get", "select",
            "whereBetweenColumns", "orWhereBetweenColumns",
            "whereNotBetweenColumns", "orWhereNotBetweenColumns",
        )
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = ClassUtils.resolveMethodReference(parameters.position) ?: return
        if (Methods[method.name]?.contains(ClassUtils.findParameterIndex(parameters.position)) != true) {
            return
        }

        if (
            !CompleteArrayValuesFor.contains(method.name)
            && parameters.position.parent.parent.elementType?.index?.toInt() == 1889 // Array value
        ) {
            return
        }

        val classes: List<String> = ClassUtils.resolveMethodClasses(method)
        if (TableOrViewCompletionProvider.BuilderClasses.none { classes.contains(it) }) {
            return
        }

        val aliases = mutableMapOf<String, String>();
        val treeMethods = ClassUtils.findMethodsInTree(method.parentOfType<Statement>()!!.firstChild)
        for (treeMethod in treeMethods) {
            if (TableOrViewCompletionProvider.Methods.contains(treeMethod.name)) {
                val tableName = (treeMethod.getParameter(0) as StringLiteralExpressionImpl).contents.trim()

                if (tableName.contains(" as ")) {
                    aliases[tableName.substringAfter("as").trim()] = tableName.substringBefore("as").trim()
                    continue
                }

                if (!TableOrViewCompletionProvider.Aliases.containsKey(treeMethod.name)) {
                    aliases[tableName] = tableName
                    continue
                }

                val aliasParam: Int = TableOrViewCompletionProvider.Aliases[treeMethod.name] ?: continue
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