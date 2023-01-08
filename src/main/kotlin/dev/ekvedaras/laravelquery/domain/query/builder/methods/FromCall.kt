package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.AliasParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.TableParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class FromCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, TableSelectionCall {
    private val rawTableParameter = reference.getParameter(0)
    private val rawAliasParameter = reference.getParameter(1)

    override val tableParameter = this.rawTableParameter.transformInstanceOf<StringLiteralExpression, TableParameter> {
        TableParameter(StringParameter(it))
    }

    private val aliasParameter = this.rawAliasParameter.transformInstanceOf<StringLiteralExpression, AliasParameter> {
        AliasParameter(StringParameter(it))
    }

    override val alias: Alias? = if (this.aliasParameter != null && this.tableParameter?.table != null) {
        Alias(name = this.aliasParameter.name, definitionParameter = this.aliasParameter.element, table = this.tableParameter.table)
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
