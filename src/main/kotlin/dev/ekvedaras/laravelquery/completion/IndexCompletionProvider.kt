package dev.ekvedaras.laravelquery.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.model.DasIndex
import com.intellij.database.model.ObjectKind
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.DatabaseUtils
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.indexesInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.tablesInParallel
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBlueprintMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForIndexes
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isIndexIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isSchemaBuilderMethod
import dev.ekvedaras.laravelquery.utils.LookupUtils.Companion.buildLookup
import dev.ekvedaras.laravelquery.utils.MethodUtils
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.containsVariable
import java.util.Collections

class IndexCompletionProvider : CompletionProvider<CompletionParameters>() {
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

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Index)
        val items = Collections.synchronizedList(mutableListOf<LookupElement>())

        complete(project, target, items)

        result.addAllElements(
            items.distinctBy { it.lookupString }
        )

        result.stopHere()
    }

    private fun complete(
        project: Project,
        target: DbReferenceExpression,
        result: MutableList<LookupElement>,
    ) {
        project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.tablesInParallel().filter { target.tablesAndAliases.contains(it.name) }.forEach { table ->
                table.indexesInParallel().forEach { result.add(it.buildLookup(project)) }
            }
        }
    }

    private fun shouldNotComplete(project: Project, method: MethodReference, parameters: CompletionParameters) =
        !ApplicationManager.getApplication().isReadAccessAllowed ||
            parameters.containsVariable() ||
            !method.isBuilderMethodForIndexes() ||
            !parameters.isIndexIn(method) ||
            parameters.isInsideRegularFunction() ||
            !method.isBlueprintMethod(project)
}
