package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.nonHashEntries
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class WhereBetweenColumnsCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val columnMethodParameter = reference.getParameter(0)
    private val otherColumnsMethodParameter = reference.getParameter(1)

    private val betweenColumns: Set<ColumnParameter> = otherColumnsMethodParameter
        .transformInstanceOf<ArrayCreationExpression, Set<ColumnParameter>> { array ->
            array.nonHashEntries().run {
                setOf(
                    elementAtOrNull(0)?.firstPsiChild?.transformInstanceOf<StringLiteralExpression, ColumnParameter> { ColumnParameter(it.asStringParameter()) },
                    elementAtOrNull(1)?.firstPsiChild?.transformInstanceOf<StringLiteralExpression, ColumnParameter> { ColumnParameter(it.asStringParameter()) },
                )
            }.filterNotNull().toSet()
        } ?: setOf()

    override val columns: Set<ColumnParameter> = setOf(
        columnMethodParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> { ColumnParameter(it.asStringParameter()) },
    ).filterNotNull().toSet() + betweenColumns
}
