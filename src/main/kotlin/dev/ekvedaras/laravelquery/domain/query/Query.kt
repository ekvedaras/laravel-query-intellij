package dev.ekvedaras.laravelquery.domain.query

import com.intellij.database.util.containsElements
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Statement
import dev.ekvedaras.laravelquery.domain.database.DataSource
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.query.builder.methods.AcceptsClosures
import dev.ekvedaras.laravelquery.domain.query.builder.methods.Alias
import dev.ekvedaras.laravelquery.domain.query.builder.methods.TableSelectionCall
import dev.ekvedaras.laravelquery.domain.query.queryVariable.InterestedInSurroundingScope
import dev.ekvedaras.laravelquery.domain.query.queryVariable.InterestedInUpperScope
import dev.ekvedaras.laravelquery.support.tap

/**
 * This class represents the whole query even if it is split across multiple statements.
 * Query class can tell which tables are being selected from, what aliases are used, etc.
 */
class Query {
    private var statements: Set<QueryStatement> = setOf()

    var models: Set<Model> = setOf()
    var dataSource: DataSource? = null
    var namespaces: Set<Namespace> = setOf()
    var tables: Set<Table> = setOf()
    var aliases: MutableMap<Alias, Table> = mutableMapOf()

    fun scanStatements(startingFrom: QueryStatement) {
        addStatement(statement = startingFrom)

        scanUp(startingFrom)
        scanAround(startingFrom)
        scanDown(startingFrom)
    }

    private fun addStatement(statement: QueryStatement) {
        if (statements.containsElements { it.statement.originalElement == statement.statement.originalElement }) {
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

        if (statement.model != null && !models.contains(statement.model)) {
            models += statement.model

            statement.model.table.tap {
                tables += it
                namespaces += it.namespace
            }
        }

        if (namespaces.isNotEmpty()) {
            dataSource = namespaces.first().dataSource
        }
    }

    private fun scanUp(startingFrom: QueryStatement) {
        if (startingFrom.queryVariable is InterestedInUpperScope) {
            startingFrom.statement.parentOfType<Function>()?.parentOfType<Statement>().tap {
                addStatement(QueryStatement(statement = it, query = this))
            }
        }
    }

    private fun scanAround(startingFrom: QueryStatement) {
        if (startingFrom.queryVariable is InterestedInSurroundingScope) {
            startingFrom.queryVariable
                .usageStatements()
                .filterNot { statements.containsElements { queryStatement -> queryStatement.statement.originalElement == it.originalElement } }
                .forEach { addStatement(QueryStatement(statement = it, query = this)) }
        }
    }

    private fun scanDown(startingFrom: QueryStatement) {
        startingFrom.callChain
            .elements
            .filterIsInstance<AcceptsClosures>()
            .forEach { methodCall ->
                methodCall.closures
                    .filter { it.shouldScan }
                    .forEach { closureParameter ->
                        closureParameter.queryParameter
                            ?.usageStatements()
                            ?.forEach { addStatement(QueryStatement(statement = it, query = this)) }
                    }
            }
    }
}
