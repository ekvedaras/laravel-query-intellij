package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasNamespace
import com.intellij.database.psi.DbDataSource
import com.intellij.openapi.project.Project
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.columnsInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.schemasInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.tables
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.tablesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.canHaveColumnsInArrayValues
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isBuilderClassMethod
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isBuilderMethodForColumns
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isColumnIn
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isInsidePhpArrayOrValue
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LookupUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LookupUtils.Companion.buildLookup
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.PsiUtils.Companion.containsVariable
import org.jetbrains.annotations.NotNull
import java.util.Collections

class ColumnCompletionProvider(private val shouldCompleteAll: Boolean = false) :
    CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return
        val project = method.project

        if (shouldNotComplete(method, parameters)) {
            return
        }

        if (parameters.isInsidePhpArrayOrValue() && !method.canHaveColumnsInArrayValues()) {
            return
        }

        if (!method.isBuilderClassMethod(project)) {
            return
        }

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Column)
        val items = Collections.synchronizedList(mutableListOf<LookupElementBuilder>())

        when (target.parts.size) {
            1 -> completeForOnePart(project, target, items, result)
            2 -> completeForTwoParts(project, target, items)
            else -> completeForThreeParts(project, target, items)
        }

        result.addAllElements(
            items.distinctBy { it.lookupString }
        )
    }

    private fun completeForOnePart(
        project: @NotNull Project,
        target: DbReferenceExpression,
        items: MutableList<LookupElementBuilder>,
        result: CompletionResultSet,
    ) {
        val schemas = target.tablesAndAliases.map { it.value.second }.filterNotNull().distinct()

        project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel().filter {
                shouldCompleteAll || schemas.isEmpty() || schemas.contains(it.name)
            }.forEach { schema ->
                addSchemaAndItsTables(items, schema, project, dataSource, target)
            }

            if (target.tablesAndAliases.isNotEmpty()) {
                addTablesAndAliases(result, target, dataSource, project, items)
            }
        }
    }

    private fun addSchemaAndItsTables(
        items: MutableList<LookupElementBuilder>,
        schema: DasNamespace,
        project: @NotNull Project,
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
        project: @NotNull Project,
        items: MutableList<LookupElementBuilder>
    ) {
        result.addLookupAdvertisement("CTRL(CMD) + SHIFT + Space to see all options")
        target.tablesAndAliases.forEach { tableAlias ->
            var lookup = LookupUtils.buildForAlias(tableAlias, dataSource)

            val table = dataSource.tables().find { table ->
                table.name == tableAlias.value.first && (tableAlias.value.second == null || table.dasParent?.name == tableAlias.value.second)
            }

            if (table != null) {
                lookup = lookup.withIcon(DasPsiWrappingSymbol(table, project).getIcon(false))

                table.columnsInParallel().forEach { column ->
                    items.add(column.buildLookup(project))
                }
            }

            items.add(lookup)
        }
    }

    private fun completeForTwoParts(
        project: @NotNull Project,
        target: DbReferenceExpression,
        result: MutableList<LookupElementBuilder>
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
        result: MutableList<LookupElementBuilder>,
        project: @NotNull Project
    ) {
        target.table.parallelStream().forEach { table ->
            val alias = target.tablesAndAliases.entries.firstOrNull { it.value.first == table.name }?.key

            table.columnsInParallel().forEach { column ->
                result.add(column.buildLookup(project, withTablePrefix = true, withSchemaPrefix = false, alias = alias))
            }
        }
    }

    private fun addTables(
        target: DbReferenceExpression,
        result: MutableList<LookupElementBuilder>,
        project: @NotNull Project
    ) {
        target.schema.parallelStream().forEach { schema ->
            schema.tablesInParallel().forEach { table ->
                result.add(table.buildLookup(project, withTablePrefix = true, triggerCompletion = true))
            }
        }
    }

    private fun completeForThreeParts(
        project: @NotNull Project,
        target: DbReferenceExpression,
        result: MutableList<LookupElementBuilder>,
    ) {
        target.table.parallelStream().forEach { table ->
            table.columnsInParallel().forEach { column ->
                result.add(column.buildLookup(project, withTablePrefix = true, withSchemaPrefix = true))
            }
        }
    }

    private fun shouldNotComplete(method: MethodReference, parameters: CompletionParameters) =
        parameters.containsVariable() ||
            !method.isBuilderMethodForColumns() ||
            !parameters.isColumnIn(method) ||
            parameters.isInsideRegularFunction()
}
