package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.RelationParameter

sealed interface SelectsRelations : QueryMethodCall {
    val relations: Set<RelationParameter>

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        relations.find { parameter.equals(it) }?.getCompletionOptions(parameter.queryMethodCall?.queryStatement?.model) ?: listOf()
}
