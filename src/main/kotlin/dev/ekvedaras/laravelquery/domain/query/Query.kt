package dev.ekvedaras.laravelquery.domain.query

import com.intellij.database.util.containsElements
import dev.ekvedaras.laravelquery.domain.database.DataSource
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.query.builder.methods.Alias
import dev.ekvedaras.laravelquery.domain.query.builder.methods.TableSelectionCall
import dev.ekvedaras.laravelquery.support.tap

class Query {
    private var statements: Set<QueryStatement> = setOf()

    var model: Model? = null
    var dataSource: DataSource? = null
    var namespaces: Set<Namespace> = setOf()
    var tables: Set<Table> = setOf()
    var aliases: MutableMap<Alias, Table> = mutableMapOf()

    fun addStatement(statement: QueryStatement) {
        if (statements.contains(statement)) {
            return
        }

        statements += statement

        statement.callChain.forEach { methodCall ->
            when (methodCall) {
                is TableSelectionCall -> {
                    val tableParameter = methodCall.tableParameter ?: return@forEach
                    val alias = methodCall.alias

                    if (alias != null) {
                        aliases[alias] = alias.table
                    } else if (tableParameter.table != null) {
                        tables += tableParameter.table
                    }

                    tableParameter.namespace.tap { namespaces += it }
                }

                else -> {}
            }
        }

        if (statement.model != null) {
            if (this.model != null && statement.model != this.model) throw Exception("Cannot re-assign query model once defined")
            this.model = statement.model

            statement.model.table.tap {
                this.tables += it
                this.namespaces += it.namespace
            }
        }

        if (this.namespaces.isNotEmpty()) {
            this.dataSource = this.namespaces.first().dataSource
        }

        if (statement.isIncompleteQuery) {
            statement.queryVariable
                ?.usageStatements()
                ?.filterNot { statements.containsElements { queryStatement -> queryStatement.statement.originalElement == it.originalElement } }
                ?.forEach { QueryStatement(statement = it, query = this) }
        }
    }
}
