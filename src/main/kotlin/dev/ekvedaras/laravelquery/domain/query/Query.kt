package dev.ekvedaras.laravelquery.domain.query

import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import dev.ekvedaras.laravelquery.domain.query.builder.methods.FromCall
import dev.ekvedaras.laravelquery.support.getNamespace

class Query {
    private var statements: List<QueryStatement> = listOf()

    private var namespaces: Set<DasNamespace> = setOf()
    private var tables: Set<DasTable> = setOf()
    private var aliases: MutableMap<String, DasTable> = mutableMapOf()

    fun addStatement(statement: QueryStatement) {
        statements += statement

        statement.callChain.forEach {methodCall ->
            when (methodCall) {
                is FromCall -> {
                    val table = methodCall.table?.asDasTable() ?: return@forEach

                    this.tables += table
                    this.namespaces += table.getNamespace()
                    this.aliases[methodCall.table.alias ?: return@forEach] = table
                }
            }
        }
    }
}
