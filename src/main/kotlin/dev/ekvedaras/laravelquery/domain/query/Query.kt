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
                    if (methodCall.tableParameter?.table != null) {
                        tables += methodCall.tableParameter.table

                        if (methodCall.tableParameter.alias != null) {
                            aliases[methodCall.tableParameter.alias] = methodCall.tableParameter.table
                        }
                    }

                    if (methodCall.tableParameter?.namespace != null) {
                        namespaces += methodCall.tableParameter.namespace
                    }

                    if (this.namespaces.isNotEmpty()) {
                        this.dataSource = this.namespaces.first().dataSource
                    }
                }
            }
        }
    }
}
