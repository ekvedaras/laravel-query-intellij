package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class WhereDateCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val columnMethodParameter = reference.getParameter(0)

    override val columns: Set<ColumnParameter> = setOf(
        columnMethodParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
            ColumnParameter(it.asStringParameter())
        }
    ).filterNotNull().toSet()
}
