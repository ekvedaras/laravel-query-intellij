package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.nonHashEntriesOfType

class GetCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val columnsMethodParameter = reference.getParameter(0)

    override val columns: Set<ColumnParameter> = when (this.columnsMethodParameter) {
        is ArrayCreationExpression -> {
            this.columnsMethodParameter
                .nonHashEntriesOfType<StringLiteralExpression>()
                .map { ColumnParameter(StringParameter(it)) }
                .toSet()
        }

        is StringLiteralExpression -> {
            setOf(ColumnParameter(StringParameter(this.columnsMethodParameter)))
        }

        else -> {
            setOf()
        }
    }
}
