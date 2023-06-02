package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.psi.DbIndex
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.schema.MigrationTable
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import dev.ekvedaras.laravelquery.support.transform

interface MigratedIndex {
    val name: String
    fun getCompletionOptions(existingTable: Table): List<LookupElement> = existingTable.indexes().map { it.asLookupElement() }.toList()
    fun findIndexReference(existingTable: Table): DbIndex? {
        if (name.isEmpty()) return null

        return existingTable.indexes().firstWhereOrNull { it.name == name }?.asDbIndex()
    }
    fun asLookupElement(existingTable: Table): LookupElement =
        existingTable.indexes().firstWhereOrNull { name == it.name }.transform { it.asLookupElement() }
            ?: LookupElementBuilder
                .create(name)
                .withTypeText(existingTable.nameWithoutPrefix, true)
    fun asLookupElement(migrationTable: MigrationTable): LookupElement =
        LookupElementBuilder
            .create(name)
            .withTypeText(migrationTable.name, true)
}
