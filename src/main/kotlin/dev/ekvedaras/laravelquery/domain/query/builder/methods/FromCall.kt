package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.AliasParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter

class FromCall(override val reference: MethodReference, override val queryStatement: QueryStatement): MethodCall {
    private val rawTableParameter = reference.getParameter(0)
    private val rawAliasParameter = reference.getParameter(1)

    val tableParameter: TableParameter? = when (this.rawTableParameter) {
        is StringLiteralExpression -> TableParameter(StringParameter(this.rawTableParameter))
        else -> null
    }

    val aliasParameter: AliasParameter? = when (this.rawAliasParameter) {
        is StringLiteralExpression -> AliasParameter(StringParameter(this.rawAliasParameter))
        else -> null
    }

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (parameter != this.tableParameter?.stringParameter) {
            return mutableListOf()
        }

        return this.tableParameter.getCompletionOptions(queryStatement)
    }
}
