package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
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

    fun table(): Table? = Table.findFirst(tableName, stringParameter.project)
    fun namespace(): Namespace? {
        return this.table()?.namespace ?: Namespace.findFirst(this.namespaceName ?: return null, stringParameter.project)
    }

    fun getCompletionOptions(queryStatement: QueryStatement): List<LookupElement> {
        val completion = mutableListOf<LookupElement>()

        if (stringParameter.isEmpty) {
            completion += Namespace.list(stringParameter.project).map { it.asLookupElement() }.toList()
            completion += Table.list(stringParameter.project).map { it.asLookupElement() }.toList()
        } else if (stringParameter.hasUncompletedPart && this.namespaceName != null) {
            completion += queryStatement.query()
                .dataSource
                ?.findNamespace(this.namespaceName)
                ?.tables()
                ?.map { it.asLookupElement() }
                ?.toList() ?: return completion
        }

        return completion
    }
}
