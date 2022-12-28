package dev.ekvedaras.laravelquery.domain.query

import com.jetbrains.php.lang.psi.elements.Statement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.MethodCall

class QueryStatement(public val statement: Statement) {
    public val callChain: List<MethodCall> = listOf()
}
