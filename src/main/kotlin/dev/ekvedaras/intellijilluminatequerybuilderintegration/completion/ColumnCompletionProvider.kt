package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.AutoPopupControllerImpl
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasTable
import com.intellij.database.symbols.DasPsiWrappingSymbol
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

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

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Column)
        result.addAllElements(
            buildCompletionList(method, parameters, target).distinctBy { it.lookupString }
        )
    }

    private fun shouldNotCompleteCurrentParameter(method: MethodReference, parameters: CompletionParameters) =
        LaravelUtils.BuilderTableColumnsParams[method.name]?.contains(
            MethodUtils.findParameterIndex(parameters.position)
        ) != true

    private fun shouldNotCompleteArrayValue(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderMethodsWithTableColumnsInArrayValues.contains(method.name)
                && parameters.position.parent.parent.elementType?.index?.toInt() == 1889

    private fun buildCompletionList(
        method: MethodReference,
        parameters: CompletionParameters,
        target: DbReferenceExpression
    ): MutableList<LookupElementBuilder> {
        val completionList = mutableListOf<LookupElementBuilder>()

//        DbUtil.getDataSources(method.project).forEach { dataSource ->
//            if (target.schema.isEmpty()) {
//                DasUtil.getSchemas(dataSource)
//                    .filter { schema ->
//                        val tables = target.table
//                        tables != null && target.tablesAndAliases.keys.any {
//                            tables.containsKey(it)
//                        }
//                    }
//                    .forEach schemas@ {
//                    completionList.add(
//                        LookupElementBuilder
//                            .create(it, it.name + ".")
//                            .withIcon(DasPsiWrappingSymbol(it, method.project).getIcon(false))
//                            .withInsertHandler { _, _ ->
//                                AutoPopupControllerImpl.getInstance(method.project).scheduleAutoPopup(parameters.editor)
//                            }
//                    )
//                }
//            }
//
//            if (target.table == null) {
//                DasUtil.getTables(dataSource)
//                    .filter { !it.isSystem && target.tablesAndAliases.containsValue(it.name) }
//                    .forEach {
//                        completionList.add(
//                            TableOrViewCompletionProvider
//                                .buildLookup(it, target.schema != null, ".", method.project) // TODO pass alias as well
//                                .withInsertHandler { _, _ ->
//                                    AutoPopupControllerImpl.getInstance(method.project).scheduleAutoPopup(parameters.editor)
//                                }
//                        )
//                    }
//            }
//
//            DasUtil.getTables(dataSource.dataSource)
//                .filter {
//                    !it.isSystem
//                            && (target.schema == null || it.dasParent?.name == target.schema?.name)
//                            && (target.table == null || it.name == target.table?.name || it.dasParent?.name == target.table?.name)
//                            && (target.tablesAndAliases.isEmpty() || target.tablesAndAliases.containsValue(it.name))
//                }
//                .forEach { addTableToCompletion(method.project, target, it, completionList) }
//        }

        return completionList
    }

    private fun addTableToCompletion(
        project: Project,
        target: DbReferenceExpression,
        table: DasTable,
        completion: MutableList<LookupElementBuilder>
    ) {
        target.tablesAndAliases
            .filter { it.value == table.name }
            .forEach { alias ->
                DasUtil.getColumns(table).forEach {
                    completion.add(
                        buildLookup(
                            project,
                            it,
                            target.tablesAndAliases.size > 1,
                            if (alias.key != alias.value) alias.key else null,
                            target,
                        )
                    )
                }
            }
    }

    private fun buildLookup(
        project: Project,
        column: DasColumn,
        prependTable: Boolean,
        alias: String? = null,
        target: DbReferenceExpression
    ): LookupElementBuilder {
        val icon = DasPsiWrappingSymbol(column, project).getIcon(false)
        var name = column.name

//        if (target.table != null) {
//            name = target.table?.name + "." + name
//        }
//
//        if (target.schema != null) {
//            name = target.schema?.name + "." + name
//        }

        val tableSchema = column.dasParent
            ?: return LookupElementBuilder
                .create(column, name)
                .withIcon(icon)

        if (!prependTable) {
            return LookupElementBuilder
                .create(column, name)
                .withIcon(icon)
        }

        if (alias != null && alias != tableSchema.name) {
            return LookupElementBuilder
                .create(column, alias + "." + column.name)
                .withIcon(icon)
                .withTailText(" (" + tableSchema.name + ")", true)
                .withTypeText(tableSchema.dasParent?.name, true)
        }

        return LookupElementBuilder
            .create(column, (alias ?: tableSchema.name) + "." + column.name)
//            .create(column, name)
            .withIcon(icon)
            .withTypeText(tableSchema.dasParent?.name, true)
    }
}