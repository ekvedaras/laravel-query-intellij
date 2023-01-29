package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.AliasParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class FromCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, TableSelectionCall {
    private val tableMethodParameter = reference.getParameter(0) as? StringLiteralExpression
    private val aliasMethodParameter = reference.getParameter(1) as? StringLiteralExpression

    override val tableParameter = this.tableMethodParameter.transformInstanceOf<StringLiteralExpression, TableParameter> {
        TableParameter(it.asStringParameter())
    }

    private val aliasParameter = this.aliasMethodParameter.transformInstanceOf<StringLiteralExpression, AliasParameter> {
        AliasParameter(it.asStringParameter())
    }

    override val alias: Alias? = if (this.aliasParameter != null && this.tableParameter?.table != null) {
        Alias(name = this.aliasParameter.name, definitionParameter = this.aliasParameter.stringParameter, table = this.tableParameter.table)
    } else if (this.tableParameter?.table != null && this.tableParameter.alias != null) {
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
