package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ClosureParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.WhenFalseClosureParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.WhenTrueClosureParameter
import dev.ekvedaras.laravelquery.support.firstChildOfType
import dev.ekvedaras.laravelquery.support.tryTransforming

class WhenCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, AcceptsClosures {
    override val closures: Set<ClosureParameter> = setOf(
        reference.getParameter(1)?.firstChildOfType<Function>().tryTransforming { WhenTrueClosureParameter(it) },
        reference.getParameter(2)?.firstChildOfType<Function>().tryTransforming { WhenFalseClosureParameter(it) },
    ).filterNotNull().toSet()

    override fun completeFor(parameter: StringParameter): List<LookupElement> = listOf()
}
