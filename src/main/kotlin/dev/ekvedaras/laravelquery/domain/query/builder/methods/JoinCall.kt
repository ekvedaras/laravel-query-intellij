package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

open class JoinCall(final override val reference: MethodReference, final override val queryStatement: QueryStatement) : QueryMethodCall, TableSelectionCall, ColumnSelectionCall {
    private val tableMethodParameter = reference.getParameter(0)
    private val firstColumnMethodParameter = reference.getParameter(1)
    private val secondColumnMethodParameter = reference.getParameter(
        if (reference.parameters.size > 3) 3 else 2
    )

    final override val tableParameter = this.tableMethodParameter.transformInstanceOf<StringLiteralExpression, TableParameter> {
        TableParameter(StringParameter(it))
    }

    override val alias: Alias? =
        if (this.tableParameter?.table != null && this.tableParameter.alias != null) Alias(
            name = this.tableParameter.alias,
            definitionParameter = this.tableParameter.stringParameter,
            table = this.tableParameter.table
        ) else null

    private val firstColumnParameter = this.firstColumnMethodParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
        ColumnParameter(StringParameter(it))
    }

    private val secondColumnParameter = this.secondColumnMethodParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
        ColumnParameter(StringParameter(it))
    }
    override val columns: Set<ColumnParameter> = setOf(this.firstColumnParameter, this.secondColumnParameter).filterNotNull().toSet()

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        return when (parameter) {
            this.tableParameter?.stringParameter -> this.tableParameter.getCompletionOptions()
            this.firstColumnParameter?.stringParameter -> this.firstColumnParameter.getCompletionOptions(queryStatement.query())
            this.secondColumnParameter?.stringParameter -> this.secondColumnParameter.getCompletionOptions(queryStatement.query())
            else -> listOf()
        }
    }

    override fun findTableReferencedIn(parameter: StringParameter): DbTable? {
        return when (parameter.element) {
            this.tableParameter?.stringParameter?.element -> super<TableSelectionCall>.findTableReferencedIn(parameter)
            this.firstColumnParameter?.stringParameter?.element, this.secondColumnParameter?.stringParameter?.element -> super<ColumnSelectionCall>.findTableReferencedIn(parameter)
            else -> null
        }
    }

    override fun findNamespaceReferencedIn(parameter: StringParameter): DbNamespace? {
        return when (parameter.element) {
            this.tableParameter?.stringParameter?.element -> super<TableSelectionCall>.findNamespaceReferencedIn(parameter)
            this.firstColumnParameter?.stringParameter?.element, this.secondColumnParameter?.stringParameter?.element -> super<ColumnSelectionCall>.findNamespaceReferencedIn(parameter)
            else -> null
        }
    }
}
