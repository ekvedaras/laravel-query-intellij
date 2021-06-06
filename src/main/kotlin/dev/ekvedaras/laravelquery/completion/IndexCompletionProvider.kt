package dev.ekvedaras.laravelquery.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.canOnlyHaveColumnsInArrayValues
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBlueprintMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForForeignKeys
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForIndexes
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForKeys
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForUniqueIndexes
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isForIndexes
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isForKeys
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isForUniqueIndexes
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isForeignKeyIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isIndexIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsidePhpArrayOrValue
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isKeyIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isUniqueIndexIn
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

        val target = DbReferenceExpression(
            parameters.position,
            when {
                method.isForIndexes() || method.isForUniqueIndexes() -> DbReferenceExpression.Companion.Type.Index
                method.isForKeys() -> DbReferenceExpression.Companion.Type.Key
                else -> DbReferenceExpression.Companion.Type.ForeignKey
            }
        )

        val items = Collections.synchronizedList(mutableListOf<LookupElement>())

        complete(project, target, items, method.isForUniqueIndexes())

        result.addAllElements(
            items.distinctBy { it.lookupString }
        )

        if (!(parameters.isInsidePhpArrayOrValue() && method.canOnlyHaveColumnsInArrayValues())) {
            result.stopHere()
        }
    }

    private fun complete(
        project: Project,
        target: DbReferenceExpression,
        result: MutableList<LookupElement>,
        isForUnique: Boolean
    ) {
        target.index.filter { it.isUnique == isForUnique }.forEach { result.add(it.buildLookup(project)) }
        target.key.forEach { result.add(it.buildLookup(project)) }
        target.foreignKey.forEach { result.add(it.buildLookup(project)) }
    }

    private fun shouldNotComplete(project: Project, method: MethodReference, parameters: CompletionParameters) =
        !ApplicationManager.getApplication().isReadAccessAllowed ||
            parameters.containsVariable() ||
            parameters.isInsidePhpArrayOrValue() ||
            (
                !method.isBuilderMethodForIndexes() &&
                    !method.isBuilderMethodForKeys() &&
                    !method.isBuilderMethodForForeignKeys() &&
                    !method.isBuilderMethodForUniqueIndexes()
                ) ||
            (
                !parameters.isIndexIn(method) &&
                    !parameters.isUniqueIndexIn(method) &&
                    !parameters.isKeyIn(method) &&
                    !parameters.isForeignKeyIn(method)
                ) ||
            parameters.isInsideRegularFunction() ||
            !method.isBlueprintMethod(project)
}
