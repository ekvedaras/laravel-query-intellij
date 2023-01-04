package dev.ekvedaras.laravelquery.v4.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.v4.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.v4.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.laravelquery.v4.utils.DatabaseUtils.Companion.schemasInParallel
import dev.ekvedaras.laravelquery.v4.utils.DatabaseUtils.Companion.tablesInParallel
import dev.ekvedaras.laravelquery.v4.utils.LaravelUtils.Companion.isBuilderMethodForTableByName
import dev.ekvedaras.laravelquery.v4.utils.LaravelUtils.Companion.isDatabaseAssertion
import dev.ekvedaras.laravelquery.v4.utils.LaravelUtils.Companion.isEloquentModel
import dev.ekvedaras.laravelquery.v4.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.v4.utils.LaravelUtils.Companion.isInteresting
import dev.ekvedaras.laravelquery.v4.utils.LaravelUtils.Companion.isTableParam
import dev.ekvedaras.laravelquery.v4.utils.LaravelUtils.Companion.isTestCase
import dev.ekvedaras.laravelquery.v4.utils.LaravelUtils.Companion.shouldCompleteOnlyColumns
import dev.ekvedaras.laravelquery.v4.utils.LaravelUtils.Companion.shouldCompleteOnlySchemas
import dev.ekvedaras.laravelquery.v4.utils.LaravelUtils.Companion.shouldCompleteSchemas
import dev.ekvedaras.laravelquery.v4.utils.LookupUtils.Companion.buildLookup
import dev.ekvedaras.laravelquery.v4.utils.MethodUtils
import dev.ekvedaras.laravelquery.v4.utils.isJoinOrRelation
import java.util.Collections

class TableOrViewCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return
        val project = method.project

        if (shouldNotComplete(project, method, parameters)) {
            return
        }

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Table)
        val items = Collections.synchronizedList(mutableListOf<LookupElement>())

        if (ApplicationManager.getApplication().isReadAccessAllowed) {
            ApplicationManager.getApplication().runReadAction {
                when (target.parts.size) {
                    1 -> populateWithOnePart(project, method, items)
                    else -> populateWithTwoParts(project, target, items)
                }
            }
        }

        result.addAllElements(
            items.distinctBy { it.lookupString }
        )

//        result.stopHere()
    }

    private fun populateWithOnePart(
        project: Project,
        method: MethodReference,
        result: MutableList<LookupElement>
    ) {
        project.dbDataSourcesInParallel().forEach dataSources@{ dataSource ->
            if (method.shouldCompleteSchemas(project)) {
                dataSource.schemasInParallel().forEach { schema ->
                    result.add(schema.buildLookup(project, dataSource))
                }

                if (method.shouldCompleteOnlySchemas()) {
                    return@dataSources
                }
            }

            dataSource.tablesInParallel().forEach { table ->
                result.add(table.buildLookup(project))
            }
        }
    }

    private fun populateWithTwoParts(
        project: Project,
        target: DbReferenceExpression,
        result: MutableList<LookupElement>,
    ) {
        target.schema.parallelStream().forEach { schema ->
            schema.tablesInParallel(project).forEach { table ->
                result.add(table.buildLookup(project, true))
            }
        }
    }

    private fun shouldNotComplete(project: Project, method: MethodReference, parameters: CompletionParameters) =
        !ApplicationManager.getApplication().isReadAccessAllowed ||
            !method.isBuilderMethodForTableByName() ||
            !parameters.isTableParam() ||
            (
                (method.isEloquentModel(project) || method.isJoinOrRelation(project)) &&
                    method.shouldCompleteOnlyColumns()
                ) ||
            (method.isTestCase(project) && !method.isDatabaseAssertion(project)) ||
            parameters.isInsideRegularFunction() ||
            !method.isInteresting(project)
}
