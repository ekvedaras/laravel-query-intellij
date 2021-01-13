package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.AutoPopupControllerImpl
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasTable
import com.intellij.database.symbols.DasPsiWrappingSymbol
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class TableOrViewCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return

        if (shouldNotCompleteCurrentParam(method, parameters)) {
            return
        }

        if (!LaravelUtils.isQueryBuilderMethod(method)) {
            return
        }

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Table)

        DbUtil.getDataSources(method.project).forEach { dataSource ->
            if (target.schema.isEmpty()) {
                DasUtil.getSchemas(dataSource).forEach {
                    result.addElement(
                        LookupElementBuilder
                            .create(it, it.name + ".")
                            .withIcon(DasPsiWrappingSymbol(it, method.project).getIcon(false))
                            .withInsertHandler { _, _ ->
                                AutoPopupControllerImpl.getInstance(method.project).scheduleAutoPopup(parameters.editor)
                            }
                    )
                }
            }

//            DasUtil.getTables(dataSource.dataSource)
//                .filter {
//                    !it.isSystem && (target.schema == null || it.dasParent?.name == target.schema?.name)
//                }
//                .forEach { result.addElement(buildLookup(it, target.schema != null, "", method.project)) }
        }
    }

    private fun shouldNotCompleteCurrentParam(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderTableMethods.contains(method.name)
                || MethodUtils.findParameterIndex(parameters.position) != 0

    companion object {
        @JvmStatic
        fun buildLookup(table: DasTable, prependSchema: Boolean, suffix: String = "", project: Project): LookupElementBuilder {
            val tableSchema = table.dasParent ?: return LookupElementBuilder
                .create(table.name + suffix)
                .withIcon(DasPsiWrappingSymbol(table, project).getIcon(false))

            if (prependSchema) {
                return LookupElementBuilder
                    .create(table, tableSchema.name + "." + table.name + suffix)
                    .withIcon(DasPsiWrappingSymbol(table, project).getIcon(false))
            }

            return LookupElementBuilder
                .create(table.name + suffix)
                .withIcon(DasPsiWrappingSymbol(table, project).getIcon(false))
                .withTailText(" (" + tableSchema.name + ")", true)
                .withLookupString(tableSchema.name + "." + table.name)
        }
    }
}