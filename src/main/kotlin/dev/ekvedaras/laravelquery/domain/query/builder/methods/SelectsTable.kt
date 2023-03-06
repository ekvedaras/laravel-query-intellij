package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import com.jetbrains.rd.util.firstOrNull
import dev.ekvedaras.laravelquery.domain.ReferencesNamespace
import dev.ekvedaras.laravelquery.domain.ReferencesTable
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.TableWithAliasParameter

sealed interface SelectsTable : QueryMethodCall, ReferencesTable, ReferencesNamespace {
    val tableParameter: TableWithAliasParameter?
    val tableAlias: TableAlias?

    override fun findTableReferencedIn(parameter: StringParameter): DbTable? =
        queryStatement
            .query
            .aliases
            .filterKeys { it.name == tableParameter?.alias }
            .filterValues { it != null }
            .filterValues { tableParameter?.namespaceName == null || tableParameter?.namespaceName == it?.namespace?.name }
            .firstOrNull()
            ?.value
            ?.asDbTable() ?: queryStatement
            .query
            .tables
            .firstOrNull { it.name == tableParameter?.tableName && (tableParameter?.namespaceName == null || tableParameter?.namespaceName == it.namespace.name) }
            ?.asDbTable()

    override fun findNamespaceReferencedIn(parameter: StringParameter): DbNamespace? =
        queryStatement
            .query
            .namespaces
            .firstOrNull { it.name == tableParameter?.namespaceName }
            ?.asDbNamespace()
}
