package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
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
        val items = Collections.synchronizedList(mutableListOf<LookupElementBuilder>())

        when (target.parts.size) {
            1 -> populateWithOnePart(project, items)
            else -> populateWithTwoParts(project, target, items)
        }

        result.addAllElements(
            items.distinctBy { it.lookupString }
        )
    }

    private fun populateWithOnePart(
        project: @NotNull Project,
        result: MutableList<LookupElementBuilder>
    ) {
        project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel().forEach { schema ->
                result.add(schema.buildLookup(project, dataSource))
            }

            dataSource.tablesInParallel().forEach { table ->
                result.add(table.buildLookup(project))
            }
        }
    }

    private fun populateWithTwoParts(
        project: @NotNull Project,
        target: DbReferenceExpression,
        result: MutableList<LookupElementBuilder>,
    ) {
        target.schema.parallelStream().forEach { schema ->
            schema.tablesInParallel().forEach { table ->
                result.add(table.buildLookup(project, true))
            }
        }
    }

    private fun shouldNotCompleteCurrentParam(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderTableMethods.contains(method.name) ||
                MethodUtils.findParameterIndex(parameters.position) != 0 ||
                (parameters.position.parent?.parent?.parent is FunctionReference && parameters.position.parent?.parent?.parent !is MethodReference)
}
