package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.util.Key
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.query.builder.methods.MethodCall
import dev.ekvedaras.laravelquery.support.descendantsOfType

val queryKey = Key<Query>("query")

class QueryStatement(val statement: Statement, query: Query? = null) {
    init {
        if (query != null) statement.putUserData(queryKey, query)
    }

    val callChain: Set<MethodCall> = if (statement.firstPsiChild is AssignmentExpression) {
        (statement.firstPsiChild as AssignmentExpression)
            .descendantsOfType<MethodReference>()
            .mapNotNull { MethodCall.from(reference = it, queryStatement = this) }
            .toSet()
    } else {
        statement.descendantsOfType<MethodReference>()
            .mapNotNull { MethodCall.from(reference = it, queryStatement = this) }
            .toSet()
    }

    val isIncompleteQuery = statement.firstPsiChild?.firstPsiChild is Variable
    val queryVariable: QueryVariable? =
        if (statement.firstPsiChild?.firstPsiChild is Variable) QueryVariable(
            variable = statement.firstPsiChild!!.firstPsiChild!! as Variable,
            query = query()
        ) else if (statement.firstPsiChild?.firstPsiChild is AssignmentExpression && statement.firstPsiChild?.firstPsiChild?.firstPsiChild is Variable) QueryVariable(
            variable = statement.firstPsiChild!!.firstPsiChild!!.firstPsiChild as Variable,
            query = query()
        ) else null

    fun query(): Query = statement.getUserData(queryKey)
        ?: Query().apply { statement.putUserData(queryKey, this) }

    init {
        this.query().addStatement(this)
    }

    fun methodCallFor(methodReference: MethodReference): MethodCall? =
        this.callChain.firstOrNull { it.reference == methodReference }
}
