package dev.ekvedaras.laravelquery.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.model.DasTable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.foreignKeysInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.indexesInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.keysInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.schemasInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.tablesInParallel
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
import java.util.function.Consumer

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

        complete(project, method, target, items)

        result.addAllElements(
            items.distinctBy { it.lookupString }
        )

        if (!(parameters.isInsidePhpArrayOrValue() && method.canOnlyHaveColumnsInArrayValues())) {
            result.stopHere()
        }
    }

    private fun complete(
        project: Project,
        method: MethodReference,
        target: DbReferenceExpression,
        result: MutableList<LookupElement>,
    ) {
        val schemas = target.tablesAndAliases.map { it.value.second }.filterNotNull().distinct()
        val tables = target.tablesAndAliases.map { it.value.first }.distinct()

        when {
            method.isForIndexes() -> completeFor(project, schemas, tables) { table ->
                table.indexesInParallel()
                    .filter { !it.isUnique }
                    .forEach { result.add(it.buildLookup(project)) }
            }
            method.isForUniqueIndexes() -> completeFor(project, schemas, tables) { table ->
                table.indexesInParallel()
                    .filter { it.isUnique }
                    .forEach { result.add(it.buildLookup(project)) }
            }
            method.isForKeys() -> completeFor(project, schemas, tables) { table ->
                table.keysInParallel().forEach { result.add(it.buildLookup(project)) }
            }
            else -> completeFor(project, schemas, tables) { table ->
                table.foreignKeysInParallel().forEach { result.add(it.buildLookup(project)) }
            }
        }
    }

    private fun completeFor(
        project: Project,
        schemas: List<String>,
        tables: List<String>,
        scanTableUsing: Consumer<DasTable>
    ) {
        project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel().filter {
                schemas.isEmpty() || schemas.contains(it.name)
            }.forEach { schema ->
                schema.tablesInParallel()
                    .filter { tables.contains(it.name) }
                    .forEach(scanTableUsing)
            }
        }
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
