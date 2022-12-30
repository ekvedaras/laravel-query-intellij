package dev.ekvedaras.laravelquery.domain.query

import dev.ekvedaras.laravelquery.domain.database.DataSource
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.query.builder.methods.FromCall

class Query {
    private var statements: Set<QueryStatement> = setOf()

    var dataSource: DataSource? = null
    var namespaces: Set<Namespace> = setOf()
    var tables: Set<Table> = setOf()
    var aliases: MutableMap<String, Table> = mutableMapOf()

    fun addStatement(statement: QueryStatement) {
        statements += statement

        statement.callChain.forEach {methodCall ->
            when (methodCall) {
                is FromCall -> {
                    val table = methodCall.table?.asDbTable() ?: return@forEach

                    this.tables += table
                    this.namespaces += table.namespace
                    this.aliases[methodCall.table.alias ?: return@forEach] = table
                    this.dataSource = table.namespace.dataSource
                }
            }
        }
    }
}
