package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import dev.ekvedaras.laravelquery.domain.ReferencesColumn
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.RelationKeyParameter

sealed interface DefinesModelRelation : QueryMethodCall, ReferencesColumn {
    val columns: Set<RelationKeyParameter>

    private fun columnParameterFor(stringParameter: StringParameter): RelationKeyParameter? =
        columns.find { stringParameter.equals(it) }

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        columnParameterFor(parameter)?.getCompletionOptions() ?: listOf()

    override fun findColumnReferencedIn(parameter: StringParameter): DbColumn? =
        columnParameterFor(parameter)?.findColumnReference()
}