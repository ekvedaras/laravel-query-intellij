package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter

sealed interface TableSelectionCall : QueryMethodCall, ReferencesTable {
    val tableParameter: TableParameter?
    val alias: Alias?

    override fun findTableReferencedIn(parameter: StringParameter): DbTable? =
        this.queryStatement
            .query
            .tables
            .firstOrNull { it.name == this.tableParameter?.tableName && (this.tableParameter?.namespaceName == null || this.tableParameter?.namespaceName == it.namespace.name ) }
            ?.asDbTable()

    override fun findNamespaceReferencedIn(parameter: StringParameter): DbNamespace? =
        this.queryStatement
            .query
            .namespaces
            .firstOrNull { it.name == this.tableParameter?.namespaceName }
            ?.asDbNamespace()
}
