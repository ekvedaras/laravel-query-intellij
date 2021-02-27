package dev.ekvedaras.intellijilluminatequerybuilderintegration.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.database.model.DasColumn
import com.intellij.openapi.project.Project
import com.intellij.psi.util.elementType
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.columnsInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.schemasInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.tables
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.tablesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LookupUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LookupUtils.Companion.buildLookup
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils
import org.jetbrains.annotations.NotNull
import java.util.*

class ColumnCompletionProvider(private val shouldCompleteAll: Boolean = false) :
    CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return
        val project = method.project

        if (shouldNotCompleteCurrentParameter(method, parameters)) {
            return
        }

        if (shouldNotCompleteArrayValue(method, parameters)) {
            return
        }

        if (!LaravelUtils.isQueryBuilderMethod(method, project)) {
            return
        }

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Column)

        when (target.parts.size) {
            1 -> completeForOnePart(project, target, result)
            2 -> completeForTwoParts(project, target, result)
            else -> completeForThreeParts(project, target, result)
        }
    }

    private fun completeForOnePart(
        project: @NotNull Project,
        target: DbReferenceExpression,
        result: CompletionResultSet
    ) {
        val schemas = target.tablesAndAliases.map { it.value.second }.filterNotNull().distinct()

        val addedSchemas = Collections.synchronizedList(mutableListOf<String>())
        val addedTables = Collections.synchronizedList(mutableListOf<String>())
        val addedColumns = Collections.synchronizedList(mutableListOf<String>())

        project.dbDataSourcesInParallel().forEach { dataSource ->
            dataSource.schemasInParallel()
                .filter {
                    (shouldCompleteAll || schemas.isEmpty() || schemas.contains(it.name)) && !addedSchemas.contains(it.name)
                }
                .forEach { schema ->
                    result.addElement(schema.buildLookup(project, dataSource))
                    addedSchemas.add(schema.name)

                    if (shouldCompleteAll || target.tablesAndAliases.isEmpty()) {
                        schema.tablesInParallel()
                            .filter { !addedTables.contains(it.name) }
                            .forEach { table ->
                                result.addElement(table.buildLookup(project))
                                addedTables.add(table.name)
                            }
                    }
                }

            if (target.tablesAndAliases.isNotEmpty()) {
                result.addLookupAdvertisement("CTRL(CMD) + SHIFT + Space to see all options")
                target.tablesAndAliases.forEach { tableAlias ->
                    var lookup = LookupUtils.buildForAlias(tableAlias, dataSource)

                    val table = dataSource.tables().find { table ->
                        table.name == tableAlias.value.first && (tableAlias.value.second == null || table.dasParent?.name == tableAlias.value.second)
                    }

                    if (table != null) {
                        lookup = lookup.withIcon(DasPsiWrappingSymbol(table, project).getIcon(false))

                        table.columnsInParallel()
                            .filter { column -> !addedColumns.contains(column.name) }
                            .forEach { column ->
                                result.addElement(column.buildLookup(project))
                                addedColumns.add(column.name)
                            }
                    }

                    result.addElement(lookup)
                }
            }
        }
    }

    private fun completeForTwoParts(
        project: @NotNull Project,
        target: DbReferenceExpression,
        result: CompletionResultSet
    ) {
        val addedTables = Collections.synchronizedList(mutableListOf<String>())
        val addedColumns = Collections.synchronizedList(mutableListOf<String>())

        project.dbDataSourcesInParallel().forEach {
            if (target.schema.isNotEmpty()) {
                target.schema.parallelStream().forEach { schema ->
                    schema.tablesInParallel()
                        .filter { !addedTables.contains(it.name) }
                        .forEach { table ->
                            result.addElement(table.buildLookup(project, true))
                            addedTables.add(table.name)
                        }
                }
            } else {
                target.table.parallelStream().forEach { table ->
                    val alias = target.tablesAndAliases.entries.firstOrNull { it.value.first == table.name }?.key

                    table.columnsInParallel()
                        .filter { !addedColumns.contains(it.name) }
                        .forEach { column ->
                            result.addElement(column.buildLookup(project, alias))
                            addedColumns.add(column.name)
                        }
                }
            }
        }
    }

    private fun completeForThreeParts(
        project: @NotNull Project,
        target: DbReferenceExpression,
        result: CompletionResultSet,
    ) {
        val addedColumns = Collections.synchronizedList(mutableListOf<String>())

        target.table.parallelStream().forEach { table ->
            table.columnsInParallel()
                .filter { it is DasColumn && !addedColumns.contains(it.name) }
                .forEach {
                    result.addElement(it.buildLookup(project))
                    addedColumns.add(it.name)
                }
        }
    }

    private fun shouldNotCompleteCurrentParameter(method: MethodReference, parameters: CompletionParameters) =
        parameters.position.textContains('$') ||
                !LaravelUtils.BuilderTableColumnsParams.containsKey(method.name) ||
                (
                        !LaravelUtils.BuilderTableColumnsParams[method.name]!!.contains(
                            MethodUtils.findParameterIndex(
                                parameters.position
                            )
                        ) &&
                                !LaravelUtils.BuilderTableColumnsParams[method.name]!!.contains(-1)
                        ) ||
                (parameters.position.parent?.parent?.parent is FunctionReference && parameters.position.parent?.parent?.parent !is MethodReference)

    private fun shouldNotCompleteArrayValue(method: MethodReference, parameters: CompletionParameters) =
        !LaravelUtils.BuilderMethodsWithTableColumnsInArrayValues.contains(method.name) &&
                (
                        parameters.position.parent.parent.elementType?.index?.toInt() == 1889 || // 1889 - array expression
                                parameters.position.parent.parent.elementType?.index?.toInt() == 805
                        ) // 805 - array value
}
