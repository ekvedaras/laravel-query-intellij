package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import com.jetbrains.rd.util.firstOrNull
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.Query
import dev.ekvedaras.laravelquery.domain.query.builder.methods.TableAlias
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

        columnOrTableOrNamespaceName = parts[0]
        tableOrNamespaceName = parts.getOrNull(1)
        namespaceName = parts.getOrNull(2)
        alias = aliasedParam.alias
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
            completion += query.aliases.keys.filterIsInstance<TableAlias>().flatMap { alias -> alias.table.columns().map { it.asLookupElement(alias = alias.name) }.toList() }

            return completion
        }

        if (stringParameter.hasThreeParts) {
            val completion = mutableListOf<LookupElement>()

            completion += query.tables.find { it.name == tableOrNamespaceName || it.name == columnOrTableOrNamespaceName }?.columns()?.map {
                it.asLookupElement(
                    withNamespacePrefix = true,
                    withTablePrefix = true
                )
            }?.toList() ?: listOf()
            completion += query.aliases.filterKeys { it.name == tableOrNamespaceName }.firstOrNull()?.value?.columns()?.map {
                it.asLookupElement(
                    withNamespacePrefix = true,
                    alias = tableOrNamespaceName
                )
            }?.toList() ?: listOf()

            return completion
        }

        if (stringParameter.hasTwoParts) {
            val namespace = query.namespaces.find { it.name == tableOrNamespaceName }
            if (namespace != null) {
                return namespace.tables().map { it.asLookupElement(triggerCompletionOnInsert = true, withNamespacePrefix = true) }.toList()
            }

            return query.tables.find { it.name == tableOrNamespaceName }?.columns()?.map { it.asLookupElement(withTablePrefix = true) }?.toList()
                ?: query.aliases.filterKeys { it is TableAlias && it.name == tableOrNamespaceName }.firstOrNull()?.value?.columns()?.map { it.asLookupElement(alias = tableOrNamespaceName) }?.toList()
                ?: listOf()
        }

        return listOf()
    }

    fun findColumnReference(query: Query): DbColumn? {
        if (stringParameter.isEmpty) return null

        if (stringParameter.hasThreeParts) {
            return query.tables
                .firstOrNull { it.name == tableOrNamespaceName && it.namespace.name == namespaceName }
                ?.columns()
                ?.firstWhereOrNull { it.name == columnOrTableOrNamespaceName }
                ?.asDbColumn()
        }

        if (stringParameter.hasTwoParts) {
            return query.tables
                .firstOrNull { it.name == tableOrNamespaceName }
                ?.columns()
                ?.firstWhereOrNull { it.name == columnOrTableOrNamespaceName }
                ?.asDbColumn()
        }

        if (stringParameter.hasOnePart) {
            return query
                .tables
                .firstOrNull { table -> table.columns().firstWhereOrNull { it.name == columnOrTableOrNamespaceName } != null }
                ?.columns()
                ?.firstWhereOrNull { it.name == columnOrTableOrNamespaceName }
                ?.asDbColumn()
        }

        return null
    }

    fun findTableReference(query: Query): DbTable? {
        if (stringParameter.isEmpty) return null

        if (stringParameter.hasThreeParts) {
            return query.tables
                .firstOrNull { it.name == tableOrNamespaceName && it.namespace.name == namespaceName }
                ?.asDbTable()
        }

        if (stringParameter.hasTwoParts) {
            return query.tables
                .firstOrNull { it.name == tableOrNamespaceName }
                ?.asDbTable()
        }

        return null
    }

    fun findNamespaceReference(query: Query): DbNamespace? {
        if (stringParameter.isEmpty) return null

        if (stringParameter.hasThreeParts) {
            return query.namespaces
                .firstOrNull { it.name == namespaceName }
                ?.asDbNamespace()
        }

        return null
    }
}
