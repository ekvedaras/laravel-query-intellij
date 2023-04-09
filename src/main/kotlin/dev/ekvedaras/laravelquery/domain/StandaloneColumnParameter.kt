package dev.ekvedaras.laravelquery.domain

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.psi.DbColumn
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.schema.MigrationTable
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import dev.ekvedaras.laravelquery.support.transform

data class StandaloneColumnParameter(val stringParameter: StringParameter) {
    fun getCompletionOptions(table: Table): List<LookupElement> = table.columns().map { it.asLookupElement() }.toList()

    fun findColumnReference(table: Table): DbColumn? {
        if (stringParameter.isEmpty) return null

        return table.columns().firstWhereOrNull { it.name == stringParameter.text }?.asDbColumn()
    }

    fun asLookupElement(table: Table): LookupElement =
        table.columns().firstWhereOrNull { stringParameter.equals(it.name) }.transform { it.asLookupElement() }
            ?: LookupElementBuilder
                .create(stringParameter.text)
                .withTypeText(table.nameWithoutPrefix, true)

    fun asLookupElement(table: MigrationTable): LookupElement =
        LookupElementBuilder
            .create(stringParameter.text)
            .withTypeText(table.name, true)
}
