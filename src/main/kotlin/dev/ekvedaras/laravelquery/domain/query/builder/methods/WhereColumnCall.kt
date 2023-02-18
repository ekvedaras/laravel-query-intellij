package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrEntriesOfType
import dev.ekvedaras.laravelquery.support.hashValuesOfType
import dev.ekvedaras.laravelquery.support.nonHashEntriesOfType
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class WhereColumnCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val firstColumnMethodParameter = reference.getParameter(0)
    private val secondColumnParameter =
        if (reference.parameters.size > 2) reference.getParameter(2)
        else reference.getParameter(1)

    override val columns: Set<ColumnParameter> = when (firstColumnMethodParameter) {
        is ArrayCreationExpression -> {
            firstColumnMethodParameter
                .hashKeysOrEntriesOfType<StringLiteralExpression>()
                .map { ColumnParameter(it.asStringParameter()) }
                .toSet() +
                firstColumnMethodParameter
                    .hashValuesOfType<StringLiteralExpression>()
                    .map { ColumnParameter(it.asStringParameter()) }
                    .toSet() +
                firstColumnMethodParameter
                    .nonHashEntriesOfType<ArrayCreationExpression>()
                    .flatMap {
                        it.nonHashEntriesOfType<StringLiteralExpression>().run {
                            if (size > 2) setOf(elementAtOrNull(0), elementAtOrNull(2))
                            else setOf(elementAtOrNull(0), elementAtOrNull(1))
                        }.filterNotNull()
                    }
                    .map { ColumnParameter(it.asStringParameter()) }
                    .toSet()
        }

        is StringLiteralExpression -> setOf(
            firstColumnMethodParameter.transform { ColumnParameter(it.asStringParameter()) },
            secondColumnParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
                ColumnParameter(it.asStringParameter())
            }
        ).filterNotNull().toSet()

        else -> {
            setOf()
        }
    }
}
