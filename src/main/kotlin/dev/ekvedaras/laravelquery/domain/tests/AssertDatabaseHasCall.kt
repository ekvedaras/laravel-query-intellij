package dev.ekvedaras.laravelquery.domain.tests

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.tests.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.tests.parameters.TableParameter
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class AssertDatabaseHasCall(reference: MethodReference) : TestMethodCall {
    override val tableParameter: TableParameter? = reference.getParameter(0)
        .transformInstanceOf<StringLiteralExpression, StringParameter> { StringParameter(it) }
        .transform { TableParameter(it) }


    // TODO: support ['<caret>']
    private val columnsMethodParameter = reference.getParameter(1) as? ArrayCreationExpression
    override val columns: Set<ColumnParameter> = columnsMethodParameter
        ?.hashElements
        ?.map { it.key }
        ?.filterIsInstance<StringLiteralExpression>()
        ?.map { ColumnParameter(StringParameter(it)) }
        ?.toSet() ?: setOf()


    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (parameter.equals(tableParameter)) return tableParameter.getCompletionOptions()

        if (tableParameter?.table == null) return listOf()
        val columnParameter = this.columnParameterFor(parameter)
        if (columnParameter != null) return columnParameter.getCompletionOptions(this.tableParameter.table)

        return listOf()
    }
}
