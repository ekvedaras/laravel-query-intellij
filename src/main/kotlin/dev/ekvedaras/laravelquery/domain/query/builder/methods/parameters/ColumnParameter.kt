package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import com.jetbrains.rd.util.firstOrNull
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.Query
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import kotlin.streams.toList

class ColumnParameter(val stringParameter: StringParameter) {
    private val columnOrTableOrNamespaceName: String?
    private val tableOrNamespaceName: String?
    private val namespaceName: String?
    val alias: String?

    init {
        val aliasedParam = stringParameter.toAliasedParam()
        val parts = aliasedParam.target.split('.').reversed()

        this.columnOrTableOrNamespaceName = parts[0]
        this.tableOrNamespaceName = parts.getOrNull(1)
        this.namespaceName = parts.getOrNull(2)
        this.alias = aliasedParam.alias
    }

    fun getCompletionOptions(query: Query): List<LookupElement> {
        if (stringParameter.isEmpty) {
            val completion = mutableListOf<LookupElement>()

            completion += query.namespaces.map { it.asLookupElement() }.toList()
            completion += query.tables.map { it.asLookupElement(triggerCompletionOnInsert = true) }.toList()
            completion += query.tables
                .flatMap { table -> table.columns().map { it.asLookupElement() }.toList() }
                .toList()
            completion += query.aliases.keys.map { it.asLookupElement() }.toList()
            completion += query.aliases.keys.flatMap { alias -> alias.table.columns().map { it.asLookupElement(alias = alias.name) }.toList() }

            return completion
        }

        if (this.stringParameter.hasThreeParts) {
            val completion = mutableListOf<LookupElement>()

            completion += query.tables.find { it.name == this.tableOrNamespaceName || it.name == this.columnOrTableOrNamespaceName }?.columns()?.map {
                it.asLookupElement(
                    withNamespacePrefix = true,
                    withTablePrefix = true
                )
            }?.toList() ?: listOf()
            completion += query.aliases.filterKeys { it.name == this.tableOrNamespaceName }.firstOrNull()?.value?.columns()?.map {
                it.asLookupElement(
                    withNamespacePrefix = true,
                    alias = this.tableOrNamespaceName
                )
            }?.toList() ?: listOf()

            return completion
        }

        if (this.stringParameter.hasTwoParts) {
            val namespace = query.namespaces.find { it.name == this.tableOrNamespaceName }
            if (namespace != null) {
                return namespace.tables().map { it.asLookupElement(triggerCompletionOnInsert = true, withNamespacePrefix = true) }.toList()
            }

            return query.tables.find { it.name == this.tableOrNamespaceName }?.columns()?.map { it.asLookupElement(withTablePrefix = true) }?.toList()
                ?: query.aliases.filterKeys { it.name == this.tableOrNamespaceName }.firstOrNull()?.value?.columns()?.map { it.asLookupElement(alias = this.tableOrNamespaceName) }?.toList()
                ?: listOf()
        }

        return listOf()
    }

    fun findColumnReference(query: Query): DbColumn? {
        if (stringParameter.isEmpty) return null

        if (stringParameter.hasThreeParts) {
            return query.tables
                .firstOrNull { it.name == this.tableOrNamespaceName && it.namespace.name == this.namespaceName }
                ?.columns()
                ?.firstWhereOrNull { it.name == this.columnOrTableOrNamespaceName }
                ?.asDbColumn()
        }

        if (stringParameter.hasTwoParts) {
            return query.tables
                .firstOrNull { it.name == this.tableOrNamespaceName }
                ?.columns()
                ?.firstWhereOrNull { it.name == this.columnOrTableOrNamespaceName }
                ?.asDbColumn()
        }

        if (stringParameter.hasOnePart) {
            return query
                .tables
                .firstOrNull { table -> table.columns().firstWhereOrNull { it.name == this.columnOrTableOrNamespaceName } != null }
                ?.columns()
                ?.firstWhereOrNull { it.name == this.columnOrTableOrNamespaceName }
                ?.asDbColumn()
        }

        return null
    }

    fun findTableReference(query: Query): DbTable? {
        if (stringParameter.isEmpty) return null

        if (stringParameter.hasThreeParts) {
            return query.tables
                .firstOrNull { it.name == this.tableOrNamespaceName && it.namespace.name == this.namespaceName }
                ?.asDbTable()
        }

        if (stringParameter.hasTwoParts) {
            return query.tables
                .firstOrNull { it.name == this.tableOrNamespaceName }
                ?.asDbTable()
        }

        return null
    }

    fun findNamespaceReference(query: Query): DbNamespace? {
        if (stringParameter.isEmpty) return null

        if (stringParameter.hasThreeParts) {
            return query.namespaces
                .firstOrNull { it.name == this.namespaceName }
                ?.asDbNamespace()
        }

        return null
    }
}
