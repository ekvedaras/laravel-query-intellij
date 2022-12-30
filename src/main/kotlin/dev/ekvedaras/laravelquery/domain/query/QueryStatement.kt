package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.util.Key
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.MethodCall

class QueryStatement(val statement: Statement) {
    val callChain: List<MethodCall> = listOf()

    fun query(): Query = statement.getUserData<Query>(Key("query"))
        ?: Query().apply { statement.putUserData<Query>(Key("query"), this) }

    init {
        this.query().addStatement(this)
    }

    fun methodCallFor(methodReference: MethodReference): MethodCall? =
        this.callChain.firstOrNull { it.reference == methodReference }
}
