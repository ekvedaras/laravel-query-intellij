package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import com.jetbrains.rd.util.firstOrNull
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.RelationParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter

sealed interface RelationSelectionCall : QueryMethodCall {
    val relations: Set<RelationParameter>

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        relations.find { parameter.equals(it) }?.getCompletionOptions(parameter.queryMethodCall?.queryStatement?.model) ?: listOf()
}
