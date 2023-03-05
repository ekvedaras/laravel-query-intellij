package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.support.firstWhereOrNull

data class RelationKeyParameter(val stringParameter: StringParameter, private val modelResolver: () -> Model?) {
    fun getCompletionOptions(): List<LookupElement> =
        modelResolver()
            ?.table
            ?.columns()
            ?.map { it.asLookupElement() }
            ?.toList() ?: listOf()

    fun findColumnReference(): DbColumn? {
        if (stringParameter.isEmpty) return null

        return modelResolver()?.table?.columns()?.firstWhereOrNull { it.name == stringParameter.text }?.asDbColumn()
    }
}
