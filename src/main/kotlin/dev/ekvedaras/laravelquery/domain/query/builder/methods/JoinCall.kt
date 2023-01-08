package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class JoinCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, TableSelectionCall {
    private val rawTableParameter = reference.getParameter(0)
    private val rawFirstColumnParameter = reference.getParameter(1)
    private val rawSecondColumnParameter = reference.getParameter(
        if (reference.parameters.size > 3) 3 else 2
    )

    override val tableParameter = this.rawTableParameter.transformInstanceOf<StringLiteralExpression, TableParameter> {
        TableParameter(StringParameter(it))
    }

    override val alias: Alias? =
        if (this.tableParameter?.table != null && this.tableParameter.alias != null) Alias(
            name = this.tableParameter.alias,
            definitionParameter = this.tableParameter.stringParameter,
            table = this.tableParameter.table
        ) else null

    private val firstColumnParameter = this.rawFirstColumnParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
        ColumnParameter(StringParameter(it))
    }

    private val secondColumnParameter = this.rawSecondColumnParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
        ColumnParameter(StringParameter(it))
    }

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        return when (parameter) {
            this.tableParameter?.stringParameter -> this.tableParameter.getCompletionOptions()
            this.firstColumnParameter?.stringParameter -> this.firstColumnParameter.getCompletionOptions(queryStatement.query())
            this.secondColumnParameter?.stringParameter -> this.secondColumnParameter.getCompletionOptions(queryStatement.query())
            else -> listOf()
        }
    }
}
