package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.StringParameter

sealed interface ColumnSelectionCall : QueryMethodCall {
    val columns: Set<ColumnParameter>

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        columns.find { it.stringParameter.element == parameter.element }?.getCompletionOptions(queryStatement.query()) ?: listOf()
}
