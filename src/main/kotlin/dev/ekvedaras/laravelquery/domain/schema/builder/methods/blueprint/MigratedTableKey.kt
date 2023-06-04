package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.psi.DbTableKey
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.schema.MigrationTable
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import dev.ekvedaras.laravelquery.support.transform

interface MigratedTableKey {
    val name: String
    val isPrimary: Boolean get() = false
    fun getCompletionOptions(existingTable: Table): List<LookupElement> = existingTable.tableKeys().map { it.asLookupElement() }.toList()
    fun findTableKeyReference(existingTable: Table): DbTableKey? {
        if (name.isEmpty()) return null

        return existingTable.tableKeys().firstWhereOrNull { it.name == name }?.asDbTableKey()
    }

    fun asLookupElement(existingTable: Table): LookupElement =
        existingTable.tableKeys().firstWhereOrNull { name == it.name }.transform { it.asLookupElement() }
            ?: LookupElementBuilder
                .create(name)
                .withTypeText(existingTable.nameWithoutPrefix, true)

    fun asLookupElement(migrationTable: MigrationTable): LookupElement =
        LookupElementBuilder
            .create(name)
            .withTypeText(migrationTable.name, true)
}
