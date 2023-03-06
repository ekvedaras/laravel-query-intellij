package dev.ekvedaras.laravelquery.domain.tests

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter
import dev.ekvedaras.laravelquery.domain.TableParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrEntriesOfType
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class AssertDatabaseHasCall(reference: MethodReference) : TestMethodCall {
    override val tableParameter: TableParameter? = reference.getParameter(0)
        .transformInstanceOf<StringLiteralExpression, StringParameter> { it.asStringParameter() }
        .transform { TableParameter(it) }


    private val columnsMethodParameter = reference.getParameter(1) as? ArrayCreationExpression
    override val columns: Set<StandaloneColumnParameter> = columnsMethodParameter
        ?.hashKeysOrEntriesOfType<StringLiteralExpression>()
        ?.map { StandaloneColumnParameter(it.asStringParameter()) }
        ?.toSet() ?: setOf()


    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (parameter.equals(tableParameter)) return tableParameter.getCompletionOptions()

        if (tableParameter?.table == null) return listOf()
        val columnParameter = columnParameterFor(parameter)
        if (columnParameter != null) return columnParameter.getCompletionOptions(tableParameter.table)

        return listOf()
    }
}
