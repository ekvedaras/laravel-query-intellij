package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.util.Key
import com.jetbrains.php.lang.psi.elements.Statement
import dev.ekvedaras.laravelquery.domain.query.queryVariable.QueryVariable

private val queryKey = Key<Query>("query")

/**
 * This class represents one query statement. It may or may not be a full query.
 * For example if we have $query = DB::table('users'); $query->get(['id']); that would be two query statements.
 */
class QueryStatement(val statement: Statement, val query: Query) {
    val callChain = QueryStatementElementCallChain.from(this)
    val queryVariable = QueryVariable.from(this)
    val model = callChain.model ?: queryVariable?.model

    companion object {
        fun from(statement: Statement) = QueryStatement(statement, Query()).also {
            it.query.scanStatements(it)
        }
    }
}
