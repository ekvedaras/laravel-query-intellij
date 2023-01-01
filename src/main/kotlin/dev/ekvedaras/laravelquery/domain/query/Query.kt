package dev.ekvedaras.laravelquery.domain.query

import com.intellij.database.util.containsElements
import dev.ekvedaras.laravelquery.domain.database.DataSource
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.query.builder.methods.Alias
import dev.ekvedaras.laravelquery.domain.query.builder.methods.TableSelectionCall

class Query {
    private var statements: Set<QueryStatement> = setOf()

    var dataSource: DataSource? = null
    var namespaces: Set<Namespace> = setOf()
    var tables: Set<Table> = setOf()
    var aliases: MutableMap<Alias, Table> = mutableMapOf()

    fun addStatement(statement: QueryStatement) {
        if (statements.contains(statement)) {
            return
        }

        statements += statement

        statement.callChain.forEach {methodCall ->
            when (methodCall) {
                is TableSelectionCall -> {
                    val tableParameter = methodCall.tableParameter ?: return@forEach
                    val alias = methodCall.alias

                    if (alias != null) {
                        aliases[alias] = alias.table
                    } else if (tableParameter.table != null) {
                        tables += tableParameter.table
                    }

                    if (tableParameter.namespace != null) {
                        namespaces += tableParameter.namespace
                    }

                    if (this.namespaces.isNotEmpty()) {
                        this.dataSource = this.namespaces.first().dataSource
                    }
                }
            }
        }

        if (statement.isIncompleteQuery) {
            statement.queryVariable
                ?.usageStatements()
                ?.filterNot { statements.containsElements { queryStatement -> queryStatement.statement.originalElement == it.originalElement } }
                ?.forEach { QueryStatement(statement = it, query = this) }
        }
    }
}
