package dev.ekvedaras.laravelquery.domain

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.support.firstWhereOrNull

data class StandaloneColumnParameter(val stringParameter: StringParameter) {
    fun getCompletionOptions(table: Table): List<LookupElement> = table.columns().map { it.asLookupElement() }.toList()

    fun findColumnReference(table: Table): DbColumn? {
        if (stringParameter.isEmpty) return null

        return table.columns().firstWhereOrNull { it.name == stringParameter.text }?.asDbColumn()
    }
}
