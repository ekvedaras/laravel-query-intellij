package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.util.Key
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.support.descendantsOfType
import dev.ekvedaras.laravelquery.support.tap
import dev.ekvedaras.laravelquery.support.transform

private val queryKey = Key<Query>("query")

class QueryStatement(val statement: Statement, query: Query? = null) {
    init {
        query.tap { statement.putUserData(queryKey, it) }
    }

    fun query(): Query = statement.getUserData(queryKey)
        ?: Query().apply { statement.putUserData(queryKey, this) }

    private val firstPsiChild = statement.firstPsiChild
    private val lastMethodReference = firstPsiChild?.descendantsOfType<MethodReference>()?.lastOrNull().run {
        if (this is MethodReference) this
        else if (firstPsiChild is MethodReference) firstPsiChild
        else null
    }

    val isIncompleteQuery = lastMethodReference?.firstPsiChild is Variable
    val queryVariable: QueryVariable? =
        if (this.isIncompleteQuery) QueryVariable(
            variable = lastMethodReference?.firstPsiChild as Variable,
            query = query()
        ) else if (lastMethodReference?.firstPsiChild is AssignmentExpression && lastMethodReference.firstPsiChild?.firstPsiChild is Variable) QueryVariable(
            variable = lastMethodReference.firstPsiChild?.firstPsiChild as Variable,
            query = query()
        ) else null

    val callChain: QueryStatementElementCallChain = QueryStatementElementCallChain.collect(
        startingFrom = firstPsiChild,
        forStatement = this,
    )

    val model: Model? = this.callChain.firstClassReference.transform { Model.from(it) }

    init {
        this.query().addStatement(this)
    }
}
