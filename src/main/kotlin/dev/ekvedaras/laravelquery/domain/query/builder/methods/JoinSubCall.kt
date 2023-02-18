package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.AliasParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class JoinSubCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, SubQuerySelectionCall, ColumnSelectionCall {
    private val aliasParameter = reference.getParameter(1).transformInstanceOf<StringLiteralExpression, AliasParameter> { AliasParameter(it.asStringParameter()) }
    private val firstColumnMethodParameter = reference.getParameter(2)
    private val secondColumnMethodParameter = reference.getParameter(
        if (reference.parameters.size > 4) 4 else 3
    )

    override val subQueryAlias: SubQueryAlias? =
        if (aliasParameter != null) SubQueryAlias(
            name = aliasParameter.name,
            definitionParameter = aliasParameter.stringParameter,
        ) else null

    private val firstColumnParameter = firstColumnMethodParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
        ColumnParameter(it.asStringParameter())
    }

    private val secondColumnParameter = secondColumnMethodParameter.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
        ColumnParameter(it.asStringParameter())
    }
    override val columns: Set<ColumnParameter> = setOf(firstColumnParameter, secondColumnParameter).filterNotNull().toSet()

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        return when (parameter) {
            firstColumnParameter?.stringParameter -> firstColumnParameter.getCompletionOptions(queryStatement.query)
            secondColumnParameter?.stringParameter -> secondColumnParameter.getCompletionOptions(queryStatement.query)
            else -> listOf()
        }
    }
}
