package dev.ekvedaras.laravelquery.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.model.DasNamespace
import com.intellij.database.psi.DbDataSource
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.columnsInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.schemasInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.tables
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.tablesInParallel
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.canHaveColumnsInArrayValues
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.canOnlyHaveColumnsInArrayValues
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBlueprintMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForColumns
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isColumnDefinitionMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isColumnIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsidePhpArrayOrValue
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInteresting
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.shouldCompleteOnlyColumns
import dev.ekvedaras.laravelquery.utils.LookupUtils
import dev.ekvedaras.laravelquery.utils.LookupUtils.Companion.buildLookup
import dev.ekvedaras.laravelquery.utils.MethodUtils
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.containsVariable
import java.util.Collections

class ColumnCompletionProvider(private val shouldCompleteAll: Boolean = false) :
    CompletionProvider<CompletionParameters>() {
    private var onlyColumns = false

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

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Column)
        val items = Collections.synchronizedList(mutableListOf<LookupElement>())

        when (target.parts.size) {
            1 -> completeForOnePart(project, target, items, method, result)
            2 -> completeForTwoParts(project, target, items)
            else -> completeForThreeParts(project, target, items)
        }

        result.addAllElements(
            items.distinctBy { it.lookupString }
        )

        result.stopHere()
    }

    private fun completeForOnePart(
        project: Project,
        target: DbReferenceExpression,
        items: MutableList<LookupElement>,
        method: MethodReference,
        result: CompletionResultSet,
    ) {
        val schemas = target.tablesAndAliases.map { it.value.second }.filterNotNull().distinct()
        onlyColumns = method.isBlueprintMethod(project) || method.isColumnDefinitionMethod(project) || method.shouldCompleteOnlyColumns()

        project.dbDataSourcesInParallel().forEach { dataSource ->
            if (!onlyColumns) {
                dataSource.schemasInParallel().filter {
                    shouldCompleteAll || schemas.isEmpty() || schemas.contains(it.name)
                }.forEach { schema ->
                    addSchemaAndItsTables(items, schema, project, dataSource, target)
                }
            }

            if (target.tablesAndAliases.isNotEmpty()) {
                addTablesAndAliases(result, target, dataSource, project, items)
            }
        }
    }

    private fun addSchemaAndItsTables(
        items: MutableList<LookupElement>,
        schema: DasNamespace,
        project: Project,
        dataSource: DbDataSource,
        target: DbReferenceExpression
    ) {
        items.add(schema.buildLookup(project, dataSource))

        if (shouldCompleteAll || target.tablesAndAliases.isEmpty()) {
            schema.tablesInParallel().forEach { table ->
                items.add(table.buildLookup(project, withTablePrefix = false, triggerCompletion = true))
            }
        }
    }

    private fun addTablesAndAliases(
        result: CompletionResultSet,
        target: DbReferenceExpression,
        dataSource: DbDataSource,
        project: Project,
        items: MutableList<LookupElement>
    ) {
        result.addLookupAdvertisement("CTRL(CMD) + SHIFT + Space to see all options")
        target.tablesAndAliases.forEach { tableAlias ->
            val table = dataSource.tables().firstOrNull { dasTable ->
                dasTable.name == tableAlias.value.first &&
                    (tableAlias.value.second == null || dasTable.dasParent?.name == tableAlias.value.second)
            }

            if (!onlyColumns) {
                items.add(
                    LookupUtils.buildForAliasOrTable(project, tableAlias, dataSource, table)
                )
            }

            table?.columnsInParallel()?.forEach { column ->
                items.add(column.buildLookup(project))
            }
        }
    }

    private fun completeForTwoParts(
        project: Project,
        target: DbReferenceExpression,
        result: MutableList<LookupElement>
    ) {
        project.dbDataSourcesInParallel().forEach {
            if (target.schema.isNotEmpty()) {
                addTables(target, result, project)
            } else {
                addTableColumns(target, result, project)
            }
        }
    }

    private fun addTableColumns(
        target: DbReferenceExpression,
        result: MutableList<LookupElement>,
        project: Project
    ) {
        target.table.parallelStream().forEach { table ->
            val alias = target.tablesAndAliases.entries
                .filter { it.value.first != it.key }
                .firstOrNull { it.value.first == table.name }?.key

            table.columnsInParallel().forEach { column ->
                result.add(column.buildLookup(project, withTablePrefix = true, withSchemaPrefix = false, alias = alias))
            }
        }
    }

    private fun addTables(
        target: DbReferenceExpression,
        result: MutableList<LookupElement>,
        project: Project
    ) {
        target.schema.parallelStream().forEach { schema ->
            schema.tablesInParallel().forEach { table ->
                result.add(table.buildLookup(project, withTablePrefix = true, triggerCompletion = true))
            }
        }
    }

    private fun completeForThreeParts(
        project: Project,
        target: DbReferenceExpression,
        result: MutableList<LookupElement>,
    ) {
        target.table.parallelStream().forEach { table ->
            table.columnsInParallel().forEach { column ->
                result.add(column.buildLookup(project, withTablePrefix = true, withSchemaPrefix = true))
            }
        }
    }

    private fun shouldNotComplete(project: Project, method: MethodReference, parameters: CompletionParameters) =
        !ApplicationManager.getApplication().isReadAccessAllowed ||
            parameters.containsVariable() ||
            !method.isBuilderMethodForColumns() ||
            !parameters.isColumnIn(method) ||
            parameters.isInsideRegularFunction() ||
            (parameters.isInsidePhpArrayOrValue() && !method.canHaveColumnsInArrayValues()) ||
            (!parameters.isInsidePhpArrayOrValue() && method.canOnlyHaveColumnsInArrayValues()) ||
            !method.isInteresting(project)
}
