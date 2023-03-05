package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrEntriesOfType
import dev.ekvedaras.laravelquery.support.transform

class InsertGetIdCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, SelectsColumns {
    private val valuesMethodParameter = reference.getParameter(0) as? ArrayCreationExpression
    private val sequenceMethodParameter = reference.getParameter(1) as? StringLiteralExpression

    override val columns: Set<ColumnParameter> =
        (valuesMethodParameter
            ?.hashKeysOrEntriesOfType<StringLiteralExpression>()
            ?.map { ColumnParameter(it.asStringParameter()) }
            ?.toSet() ?: setOf()) +
            (sequenceMethodParameter.transform {
                setOf(ColumnParameter(it.asStringParameter()))
            } ?: setOf())
}
