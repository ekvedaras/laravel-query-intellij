package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter

class JoinCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : MethodCall, TableSelectionCall {
    private val rawTableParameter = reference.getParameter(0)

    override val tableParameter: TableParameter? = when (this.rawTableParameter) {
        is StringLiteralExpression -> TableParameter(StringParameter(this.rawTableParameter))
        else -> null
    }

    override val alias: Alias? = if (this.tableParameter?.table != null && this.tableParameter.alias != null) {
        Alias(name = this.tableParameter.alias, definitionParameter = this.tableParameter.stringParameter, table = this.tableParameter.table)
    } else {
        null
    }

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (parameter != this.tableParameter?.stringParameter) {
            return mutableListOf()
        }

        return this.tableParameter.getCompletionOptions()
    }
}
