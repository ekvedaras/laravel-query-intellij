package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.schemasInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.tablesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LookupUtils.Companion.buildLookup
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
        val project = method.project

        if (shouldNotCompleteCurrentParam(method, parameters)) {
            return
        }

        if (!LaravelUtils.isQueryBuilderMethod(method, project)) {
            return
        }

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Table)

        when (target.parts.size) {
            1 -> populateWithOnePart(project, result)
            else -> populateWithTwoParts(project, target, result)
        }
    }

    private fun populateWithOnePart(
        project: @NotNull Project,
        result: CompletionResultSet
    ) {
        val addedSchemas = Collections.synchronizedList(mutableListOf<String>())
        val addedTables = Collections.synchronizedList(mutableListOf<String>())

        project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel()
                .filter { schema -> !addedSchemas.contains(schema.name) }
                .forEach { schema ->
                    addedSchemas.add(schema.name)
                    result.addElement(schema.buildLookup(project, dataSource))
                }

            dataSource.tablesInParallel()
                .filter { table -> !addedTables.contains(table.name) }
                .forEach { table ->
                    addedTables.add(table.name)
                    result.addElement(table.buildLookup(project))
                }
        }
    }

    private fun populateWithTwoParts(
        project: @NotNull Project,
        target: DbReferenceExpression,
        result: CompletionResultSet,
    ) {
        val addedTables = Collections.synchronizedList(mutableListOf<String>())

        target.schema.parallelStream().forEach { schema ->
            schema.tablesInParallel()
                .filter { table -> !addedTables.contains(table.name) }
                .forEach { table ->
                    addedTables.add(table.name)
                    result.addElement(table.buildLookup(project))
                }
        }
    }

    private fun shouldNotCompleteCurrentParam(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderTableMethods.contains(method.name) ||
                MethodUtils.findParameterIndex(parameters.position) != 0 ||
                (parameters.position.parent?.parent?.parent is FunctionReference && parameters.position.parent?.parent?.parent !is MethodReference)
}
