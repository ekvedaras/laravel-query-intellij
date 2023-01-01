package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter

class JoinCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : MethodCall, TableSelectionCall {
    private val rawTableParameter = reference.getParameter(0)
    private val rawFirstColumnParameter = reference.getParameter(1)
    private val rawSecondColumnParameter = reference.getParameter(
        if (reference.parameters.size > 3) 3 else 2
    )

    override val tableParameter: TableParameter? =
        if (this.rawTableParameter is StringLiteralExpression) TableParameter(
            StringParameter(this.rawTableParameter)
        ) else null

    override val alias: Alias? =
        if (this.tableParameter?.table != null && this.tableParameter.alias != null) Alias(
            name = this.tableParameter.alias,
            definitionParameter = this.tableParameter.stringParameter,
            table = this.tableParameter.table
        ) else null

    val firstColumnParameter: ColumnParameter? =
        if (this.rawFirstColumnParameter is StringLiteralExpression) ColumnParameter(
            StringParameter(this.rawFirstColumnParameter)
        ) else null

    val secondColumnParameter: ColumnParameter? =
        if (this.rawSecondColumnParameter is StringLiteralExpression) ColumnParameter(
            StringParameter(this.rawSecondColumnParameter)
        ) else null

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        return when (parameter) {
            this.tableParameter?.stringParameter -> this.tableParameter.getCompletionOptions()
            this.firstColumnParameter?.stringParameter -> this.firstColumnParameter.getCompletionOptions(queryStatement.query())
            this.secondColumnParameter?.stringParameter -> this.secondColumnParameter.getCompletionOptions(queryStatement.query())
            else -> listOf()
        }
    }
}
