package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrFirstEntryOfType
import dev.ekvedaras.laravelquery.support.nonHashEntriesOfType
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class UpsertCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, SelectsColumns {
    private val valuesMethodParameter = reference.getParameter(0) as? ArrayCreationExpression
    private val uniqueByMethodParameter = reference.getParameter(1)
    private val updateMethodParameter = reference.getParameter(2) as? ArrayCreationExpression

    override val columns: Set<ColumnParameter> = (valuesMethodParameter.transform { rows ->
        rows
            .nonHashEntriesOfType<ArrayCreationExpression>()
            .flatMap { row ->
                row
                    .hashKeysOrFirstEntryOfType<StringLiteralExpression>()
                    .map { ColumnParameter(it.asStringParameter()) }
            }.toSet()
    } ?: setOf()) +
        (uniqueByMethodParameter.transformInstanceOf<StringLiteralExpression, Set<ColumnParameter>> {
            setOf(ColumnParameter(it.asStringParameter()))
        } ?: setOf()) +
        (uniqueByMethodParameter.transformInstanceOf<ArrayCreationExpression, Set<ColumnParameter>> {
            it.nonHashEntriesOfType<StringLiteralExpression>().map { ColumnParameter(it.asStringParameter()) }.toSet()
        } ?: setOf()) +
        (updateMethodParameter.transform {
            it.nonHashEntriesOfType<StringLiteralExpression>().map { ColumnParameter(it.asStringParameter()) }.toSet()
        } ?: setOf())
}
