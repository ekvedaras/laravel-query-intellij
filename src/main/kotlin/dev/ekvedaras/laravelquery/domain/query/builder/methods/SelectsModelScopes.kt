package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ScopeParameter

sealed interface SelectsModelScopes : QueryMethodCall {
    val scopes: Set<ScopeParameter>

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        scopes.find { parameter.equals(it) }?.getCompletionOptions(parameter.queryMethodCall?.queryStatement?.model) ?: listOf()
}
