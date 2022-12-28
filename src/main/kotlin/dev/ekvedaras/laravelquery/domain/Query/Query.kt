package dev.ekvedaras.laravelquery.domain.Query

import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.psi.DbDataSource
import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import com.intellij.database.util.DbUtil
import dev.ekvedaras.laravelquery.domain.Query.Builder.Methods.FromCall
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.getNamespace

class Query {
    private var statements: List<QueryStatement> = listOf()

    val datasource: DbDataSource? = null
    var namespaces: Set<DasNamespace> = setOf()
    var tables: Set<DasTable> = setOf()
    val aliases: Map<String, DasTable> = mapOf()

    fun addStatement(statement: QueryStatement) {
        statements += statement

        statement.callChain.forEach {methodCall ->
            when (methodCall) {
                is FromCall -> this.tables += (methodCall.table?.asDasTable() ?: return@forEach).also { this.namespaces += it.getNamespace() }
            }
        }
    }
}
