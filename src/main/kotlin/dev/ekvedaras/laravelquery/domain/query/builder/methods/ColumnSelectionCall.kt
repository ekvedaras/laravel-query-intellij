package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.StringParameter

interface ColumnSelectionCall : MethodCall {
    val columns: Set<ColumnParameter>

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        columns.find { it.stringParameter.element == parameter.element }?.getCompletionOptions(queryStatement.query()) ?: listOf()
}
