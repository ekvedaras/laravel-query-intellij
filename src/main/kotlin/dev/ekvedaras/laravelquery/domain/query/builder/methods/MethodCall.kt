package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.StringParameter

interface MethodCall {
    val reference: MethodReference
    val queryStatement: QueryStatement

    fun completeFor(parameter: StringParameter): List<LookupElement>
}
