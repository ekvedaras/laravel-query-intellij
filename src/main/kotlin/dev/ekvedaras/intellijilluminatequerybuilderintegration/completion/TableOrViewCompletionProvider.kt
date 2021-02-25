package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.DeclarativeInsertHandler
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasObject
import com.intellij.database.model.DasTable
import com.intellij.openapi.project.Project
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.schemasInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.tablesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LookupUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils
import org.jetbrains.annotations.NotNull
import java.util.*

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

        val project = method.project

        if (!LaravelUtils.isQueryBuilderMethod(method, project)) {
            return
        }

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Table)

        if (target.parts.size == 1) {
            populateWithOnePart(project, result)
        } else if (target.parts.size == 2) {
            populateWithTwoParts(target, result, project)
        }
    }

    private fun populateWithTwoParts(
        target: DbReferenceExpression,
        result: CompletionResultSet,
        project: @NotNull Project
    ) {
        val addedTables = mutableListOf<String>()
        val syncAddedTables = Collections.synchronizedList(addedTables)

        target.schema
            .parallelStream()
            .forEach { schema ->
                schema.tablesInParallel()
                    .filter { shouldShowTable(it, target, addedTables) }
                    .forEach {
                        result.addElement(LookupUtils.forTable(project, it as DasTable, true))
                        syncAddedTables.add(it.name)
                    }
            }
    }

    private fun shouldShowTable(
        it: DasObject?,
        target: DbReferenceExpression,
        addedTables: MutableList<String>
    ) = !(it as DasTable).isSystem && target.schema.contains(it.dasParent) && !addedTables.contains(it.name)

    private fun populateWithOnePart(
        project: @NotNull Project,
        result: CompletionResultSet
    ) {
        val addedSchemas = mutableListOf<String>()
        val syncAddedSchemas = Collections.synchronizedList(addedSchemas)
        val addedTables = mutableListOf<String>()
        val syncAddedTables = Collections.synchronizedList(addedTables)

        project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel()
                .filter { !addedSchemas.contains(it.name) }
                .forEach {
                    result.addElement(
                        LookupElementBuilder
                            .create(it, it.name)
                            .withIcon(DasPsiWrappingSymbol(it, project).getIcon(false))
                            .withTypeText(dataSource.name, true)
                            .withInsertHandler(
                                DeclarativeInsertHandler.Builder()
                                    .disableOnCompletionChars(".")
                                    .insertOrMove(".")
                                    .triggerAutoPopup()
                                    .build()
                            )
                    )

                    syncAddedSchemas.add(it.name)
                }

            dataSource.tablesInParallel()
                .filter { !it.isSystem && !addedTables.contains(it.name) }
                .forEach {
                    result.addElement(
                        LookupElementBuilder
                            .create(it, it.name)
                            .withIcon(DasPsiWrappingSymbol(it, project).getIcon(false))
                            .withTailText(" (" + it.dasParent?.name + ")", true)
                            .withTypeText(dataSource.name, true)
                            .withInsertHandler(
                                DeclarativeInsertHandler.Builder().build()
                            )
                    )

                    syncAddedTables.add(it.name)
                }
        }
    }

    private fun shouldNotCompleteCurrentParam(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderTableMethods.contains(method.name) ||
                MethodUtils.findParameterIndex(parameters.position) != 0 ||
                (parameters.position.parent?.parent?.parent is FunctionReference && parameters.position.parent?.parent?.parent !is MethodReference)
}
