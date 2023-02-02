package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.util.Key
import com.jetbrains.php.lang.psi.elements.Statement
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.support.transform

private val queryKey = Key<Query>("query")

/**
 * This class represents one query statement. It may or may not be a full query.
 * For example if we have $query = DB::table('users'); $query->get(['id']); that would be two query statements.
 */
class QueryStatement(val statement: Statement, val query: Query) {
    private val firstPsiChild = statement.firstPsiChild

    val callChain: QueryStatementElementCallChain = QueryStatementElementCallChain.collect(
        startingFrom = firstPsiChild,
        forStatement = this,
    )

    val queryVariable: QueryVariable? = QueryVariable.from(this)
    val model: Model? = queryVariable?.model ?: callChain.firstClassReference.transform { Model.from(it) }

    companion object {
        fun Statement.query(): Query = getUserData(queryKey) ?: Query().also { putUserData(queryKey, it) }
        fun from(statement: Statement) = QueryStatement(statement, statement.query()).also {
            statement.query().scanStatements(it)
        }
    }
}
