package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import com.jetbrains.rd.util.firstOrNull
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.RelationParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ScopeParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter

sealed interface ScopeSelectionCall : QueryMethodCall {
    val scopes: Set<ScopeParameter>

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        scopes.find { parameter.equals(it) }?.getCompletionOptions(parameter.queryMethodCall?.queryStatement?.model) ?: listOf()
}
