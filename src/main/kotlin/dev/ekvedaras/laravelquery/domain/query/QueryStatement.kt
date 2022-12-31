package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.util.Key
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.MethodCall
import dev.ekvedaras.laravelquery.support.descendantsOfType

class QueryStatement(val statement: Statement) {
    val callChain: Set<MethodCall> = statement.descendantsOfType<MethodReference>()
        .mapNotNull { MethodCall.from(reference = it, queryStatement = this) }
        .toSet()

    fun query(): Query = statement.getUserData(Key("query"))
        ?: Query().apply { statement.putUserData(Key("query"), this) }

    init {
        this.query().addStatement(this)
    }

    fun methodCallFor(methodReference: MethodReference): MethodCall? =
        this.callChain.firstOrNull { it.reference == methodReference }
}
