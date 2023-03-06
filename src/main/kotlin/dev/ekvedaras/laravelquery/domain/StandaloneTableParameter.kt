package dev.ekvedaras.laravelquery.domain

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.database.Table

data class StandaloneTableParameter(val stringParameter: StringParameter) {
    private val tableName = stringParameter.text
    val table: Table? = Table.findFirst(tableName, stringParameter.project)

    fun getCompletionOptions(): List<LookupElement> =
        Table.list(stringParameter.project).map { it.asLookupElement() }.toList()
}
