package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ScopeParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class NewQueryWithoutScopeCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ScopeSelectionCall {
    override val scopes: Set<ScopeParameter> = reference.getParameter(0).transformInstanceOf<StringLiteralExpression, Set<ScopeParameter>> {
        setOf(ScopeParameter(it.asStringParameter()))
    } ?: setOf()
}
