package dev.ekvedaras.laravelquery.domain.Query

import com.jetbrains.php.lang.psi.elements.Statement
import dev.ekvedaras.laravelquery.domain.Query.Builder.Methods.MethodCall

class QueryStatement(public val statement: Statement) {
    public val callChain: List<MethodCall> = listOf()
}
