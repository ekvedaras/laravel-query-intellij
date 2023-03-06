package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.AliasParameter
import dev.ekvedaras.laravelquery.domain.TableWithAliasParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class FromCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, SelectsTable {
    private val tableMethodParameter = reference.getParameter(0) as? StringLiteralExpression
    private val aliasMethodParameter = reference.getParameter(1) as? StringLiteralExpression

    override val tableParameter = tableMethodParameter.transformInstanceOf<StringLiteralExpression, TableWithAliasParameter> {
        TableWithAliasParameter(it.asStringParameter())
    }

    private val aliasParameter = aliasMethodParameter.transformInstanceOf<StringLiteralExpression, AliasParameter> {
        AliasParameter(it.asStringParameter())
    }

    override val tableAlias: TableAlias? = if (aliasParameter != null && tableParameter?.table != null) {
        TableAlias(name = aliasParameter.name, definitionParameter = aliasParameter.stringParameter, table = tableParameter.table)
    } else if (tableParameter?.table != null && tableParameter.alias != null) {
        TableAlias(name = tableParameter.alias, definitionParameter = tableParameter.stringParameter, table = tableParameter.table)
    } else {
        null
    }

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (parameter != tableParameter?.stringParameter) {
            return mutableListOf()
        }

        return tableParameter.getCompletionOptions()
    }
}
