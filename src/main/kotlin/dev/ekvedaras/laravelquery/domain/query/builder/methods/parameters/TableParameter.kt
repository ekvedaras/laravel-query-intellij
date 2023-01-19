package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.database.Table
import kotlin.streams.toList

class TableParameter(val stringParameter: StringParameter) {
    val tableName: String
    val namespaceName: String?
    val alias: String?

    init {
        val aliasedParam = stringParameter.toAliasedParam()
        val parts = aliasedParam.target.split('.').reversed()

        this.tableName = parts[0]
        this.namespaceName = parts.getOrNull(1)
        this.alias = aliasedParam.alias
    }

    val namespace: Namespace? = if (this.namespaceName != null) {
        Namespace.findFirst(this.namespaceName, stringParameter.project)
    } else {
        Table.findFirst(this.tableName, stringParameter.project)?.namespace
    }

    val table: Table? = namespace?.findTable(this.tableName) ?: Table.findFirst(this.tableName, stringParameter.project)

    fun getCompletionOptions(): List<LookupElement> {
        val completion = mutableListOf<LookupElement>()

        if (stringParameter.isEmpty) {
            completion += Namespace.list(stringParameter.project).map { it.asLookupElement() }.toList()
            completion += Table.list(stringParameter.project).map { it.asLookupElement() }.toList()
        } else if (stringParameter.hasUncompletedPart && this.namespace != null) {
            completion += this.namespace
                .tables()
                .map { it.asLookupElement(withNamespacePrefix = true) }
                .toList()
        }

        return completion
    }
}
