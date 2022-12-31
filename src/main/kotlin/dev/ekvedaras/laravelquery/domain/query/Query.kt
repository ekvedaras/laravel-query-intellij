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
                    methodCall.tableParameter ?: return@forEach

                    methodCall.tableParameter.table().apply {
                        if (this != null) {
                            tables += this
                            aliases[methodCall.tableParameter.alias ?: return@forEach] = this
                        }
                    }
                    methodCall.tableParameter.namespace().apply { if (this != null) namespaces += this }

                    if (this.namespaces.isNotEmpty()) {
                        this.dataSource = this.namespaces.first().dataSource
                    }
                }
            }
        }
    }
}
