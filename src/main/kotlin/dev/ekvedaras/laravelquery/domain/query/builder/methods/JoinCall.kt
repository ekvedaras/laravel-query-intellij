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

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (parameter == this.tableParameter?.stringParameter) {
            return this.tableParameter.getCompletionOptions()
        }

        if (parameter == this.firstColumnParameter?.stringParameter) {
            return this.firstColumnParameter.getCompletionOptions(queryStatement.query())
        }

        return listOf()
    }
}
