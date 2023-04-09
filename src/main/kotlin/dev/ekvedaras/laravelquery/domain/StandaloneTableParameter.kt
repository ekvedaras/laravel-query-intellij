package dev.ekvedaras.laravelquery.domain

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import dev.ekvedaras.laravelquery.domain.database.Table

data class StandaloneTableParameter(val stringParameter: StringParameter) {
    val tableName = stringParameter.text
    val table: Table? = Table.findFirst(tableName, stringParameter.project)

    fun getCompletionOptions(): List<LookupElement> =
        Table.list(stringParameter.project).map { it.asLookupElement() }.toList()

    fun asLookupElement(): LookupElement = LookupElementBuilder.create(stringParameter.text)
}
