package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.query.Query
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
            completion += query.tables.map { it.asLookupElement() }.toList()
            completion += query.tables
                .flatMap { table -> table.columns().map { it.asLookupElement() }.toList() }
                .toList()

            return completion
        }

        if (this.namespaceName != null) {
            return query.namespaces.find { it.name == this.namespaceName }?.tables()?.map { it.asLookupElement() }?.toList() ?: listOf()
        }

        if (this.tableOrNamespaceName != null) {
            val completion = mutableListOf<LookupElement>()

            completion += query.namespaces.find { it.name == this.tableOrNamespaceName }?.tables()?.map { it.asLookupElement() }?.toList() ?: listOf()
            completion += query.tables.find { it.name == this.tableOrNamespaceName }?.columns()?.map { it.asLookupElement() }?.toList() ?: listOf()
            completion += query.aliases[this.tableOrNamespaceName]?.columns()?.map { it.asLookupElement(alias = this.tableOrNamespaceName) }?.toList() ?: listOf()

            return completion
        }

        return listOf()
    }
}
