package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.nonHashEntriesOfType

class WhereRowValuesCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val columnsMethodParameter = reference.getParameter(0) as? ArrayCreationExpression

    override val columns: Set<ColumnParameter> = columnsMethodParameter
        ?.nonHashEntriesOfType<StringLiteralExpression>()
        ?.map { ColumnParameter(it.asStringParameter()) }
        ?.toSet() ?: setOf()
}
