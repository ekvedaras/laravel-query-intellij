package dev.ekvedaras.laravelquery.domain.tests

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.TableParameter
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class AssertDatabaseCountCall(reference: MethodReference) : TestMethodCall {
    override val tableParameter: TableParameter? = reference.getParameter(0)
        .transformInstanceOf<StringLiteralExpression, StringParameter> { it.asStringParameter() }
        .transform { TableParameter(it) }


    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (parameter.equals(tableParameter)) return tableParameter.getCompletionOptions()

        return listOf()
    }
}
