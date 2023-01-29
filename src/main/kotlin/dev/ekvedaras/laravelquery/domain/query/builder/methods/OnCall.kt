package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.transform

class OnCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val firstColumnMethodParameter = reference.getParameter(0) as? StringLiteralExpression
    private val secondColumnMethodParameter = reference.getParameter(
        if (reference.parameters.size > 2) 2 else 1
    ) as? StringLiteralExpression

    override val columns: Set<ColumnParameter> = setOf(
        this.firstColumnMethodParameter.transform { ColumnParameter(it.asStringParameter()) },
        this.secondColumnMethodParameter.transform { ColumnParameter(it.asStringParameter()) },
    ).filterNotNull().toSet()
}
