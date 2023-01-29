package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrEntriesOfType
import dev.ekvedaras.laravelquery.support.nonHashEntriesOfType

class WhereCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val columnsMethodParameter = reference.getParameter(0)

    override val columns: Set<ColumnParameter> = when (this.columnsMethodParameter) {
        is ArrayCreationExpression -> {
            this.columnsMethodParameter
                .hashKeysOrEntriesOfType<StringLiteralExpression>()
                .map { ColumnParameter(it.asStringParameter()) }
                .toSet() +
                this.columnsMethodParameter
                    .nonHashEntriesOfType<ArrayCreationExpression>()
                    .mapNotNull { it.nonHashEntriesOfType<StringLiteralExpression>().firstOrNull() }
                    .map { ColumnParameter(it.asStringParameter()) }
                    .toSet()
        }

        is StringLiteralExpression -> {
            reference.parameters.filterIsInstance<StringLiteralExpression>().map { ColumnParameter(it.asStringParameter()) }.toSet()
        }

        else -> {
            setOf()
        }
    }
}
