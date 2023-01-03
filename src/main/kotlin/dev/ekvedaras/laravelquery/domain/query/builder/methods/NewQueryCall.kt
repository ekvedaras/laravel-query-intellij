package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.StringParameter

class NewQueryCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall {
    override fun completeFor(parameter: StringParameter): List<LookupElement> = listOf()
}
