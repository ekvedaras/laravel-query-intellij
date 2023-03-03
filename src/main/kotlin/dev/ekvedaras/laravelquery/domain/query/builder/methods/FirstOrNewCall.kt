package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrFirstEntryOfType

class FirstOrNewCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val attributesMethodParameter = reference.getParameter(0) as? ArrayCreationExpression
    private val valuesMethodParameter = reference.getParameter(1) as? ArrayCreationExpression

    override val columns: Set<ColumnParameter> = (attributesMethodParameter
        ?.hashKeysOrFirstEntryOfType<StringLiteralExpression>()
        ?.map { ColumnParameter(it.asStringParameter()) }
        ?.toSet() ?: setOf()) +
        (valuesMethodParameter
            ?.hashKeysOrFirstEntryOfType<StringLiteralExpression>()
            ?.map { ColumnParameter(it.asStringParameter()) }
            ?.toSet() ?: setOf())
}
