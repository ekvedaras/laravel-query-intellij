package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.nonHashEntriesOfType

class GetCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, SelectsColumns {
    private val columnsMethodParameter = reference.getParameter(0)

    override val columns: Set<ColumnParameter> = when (columnsMethodParameter) {
        is ArrayCreationExpression -> {
            columnsMethodParameter
                .nonHashEntriesOfType<StringLiteralExpression>()
                .map { ColumnParameter(it.asStringParameter()) }
                .toSet()
        }

        is StringLiteralExpression -> {
            setOf(ColumnParameter(columnsMethodParameter.asStringParameter()))
        }

        else -> {
            setOf()
        }
    }
}
