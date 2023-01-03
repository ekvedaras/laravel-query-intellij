package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.support.elementsOfType

class GetCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val columnsParameter = reference.getParameter(0)

    override val columns: Set<ColumnParameter> = when (this.columnsParameter) {
        is ArrayCreationExpression -> {
            this.columnsParameter.elementsOfType<StringLiteralExpression>().map { ColumnParameter(StringParameter(it)) }.toSet()
        }

        is StringLiteralExpression -> {
            setOf(ColumnParameter(StringParameter(this.columnsParameter)))
        }

        else -> {
            setOf()
        }
    }
}
