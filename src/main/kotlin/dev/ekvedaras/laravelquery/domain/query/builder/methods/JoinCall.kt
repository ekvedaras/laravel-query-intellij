package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.TableWithAliasParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class JoinCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, SelectsTable, SelectsColumns {
    private val tableMethodParameter = reference.getParameter(0)
    private val firstColumnMethodParameter = reference.getParameter(1)
    private val secondColumnMethodParameter = reference.getParameter(
        if (reference.parameters.size > 3) 3 else 2
    )

    final override val tableParameter = tableMethodParameter.transformInstanceOf<StringLiteralExpression, TableWithAliasParameter> {
        TableWithAliasParameter(it.asStringParameter())
    }

    override val tableAlias: TableAlias? =
        if (tableParameter?.table != null && tableParameter.alias != null) TableAlias(
            name = tableParameter.alias,
            definitionParameter = tableParameter.stringParameter,
            table = tableParameter.table
        ) else null

    private val firstColumnParameter = firstColumnMethodParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
        ColumnParameter(it.asStringParameter())
    }

    private val secondColumnParameter = secondColumnMethodParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
        ColumnParameter(it.asStringParameter())
    }
    override val columns: Set<ColumnParameter> = setOf(firstColumnParameter, secondColumnParameter).filterNotNull().toSet()

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        return when (parameter) {
            tableParameter?.stringParameter -> tableParameter.getCompletionOptions()
            firstColumnParameter?.stringParameter -> firstColumnParameter.getCompletionOptions(queryStatement.query)
            secondColumnParameter?.stringParameter -> secondColumnParameter.getCompletionOptions(queryStatement.query)
            else -> listOf()
        }
    }

    override fun findTableReferencedIn(parameter: StringParameter): DbTable? {
        return when (parameter.element) {
            tableParameter?.stringParameter?.element -> super<SelectsTable>.findTableReferencedIn(parameter)
            firstColumnParameter?.stringParameter?.element, secondColumnParameter?.stringParameter?.element -> super<SelectsColumns>.findTableReferencedIn(parameter)
            else -> null
        }
    }

    override fun findNamespaceReferencedIn(parameter: StringParameter): DbNamespace? {
        return when (parameter.element) {
            tableParameter?.stringParameter?.element -> super<SelectsTable>.findNamespaceReferencedIn(parameter)
            firstColumnParameter?.stringParameter?.element, secondColumnParameter?.stringParameter?.element -> super<SelectsColumns>.findNamespaceReferencedIn(parameter)
            else -> null
        }
    }
}
