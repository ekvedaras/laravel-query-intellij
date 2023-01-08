package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import kotlin.streams.toList

class CreateCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall {
    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (parameter.parentMethodParameter != reference.getParameter(0)) return listOf()

        return this.queryStatement.query().model?.table?.columns()?.map { it.asLookupElement() }?.toList() ?: listOf()
    }
}
