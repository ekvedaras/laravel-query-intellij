package dev.ekvedaras.laravelquery.domain

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.database.Table

class TableWithAliasParameter(val stringParameter: StringParameter) {
    val tableName: String
    val namespaceName: String?
    val alias: String?

    init {
        val aliasedParam = stringParameter.toAliasedParam()
        val parts = aliasedParam.target.split('.').reversed()

        tableName = parts[0]
        namespaceName = parts.getOrNull(1)
        alias = aliasedParam.alias
    }

    val namespace: Namespace? = if (namespaceName != null) {
        Namespace.findFirst(namespaceName, stringParameter.project)
    } else {
        Table.findFirst(tableName, stringParameter.project)?.namespace
    }

    val table: Table? = namespace?.findTable(tableName) ?: Table.findFirst(tableName, stringParameter.project)

    fun getCompletionOptions(): List<LookupElement> {
        val completion = mutableListOf<LookupElement>()

        if (stringParameter.isEmpty) {
            completion += Namespace.list(stringParameter.project).map { it.asLookupElement() }.toList()
            completion += Table.list(stringParameter.project).map { it.asLookupElement() }.toList()
        } else if (stringParameter.hasUncompletedPart && namespace != null) {
            completion += namespace
                .tables()
                .map { it.asLookupElement(withNamespacePrefix = true) }
                .toList()
        }

        return completion
    }
}
