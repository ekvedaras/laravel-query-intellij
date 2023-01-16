package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrEntriesOfType
import kotlin.streams.toList

class CreateCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val columnsMethodParameter = reference.getParameter(0) as? ArrayCreationExpression

    override val columns: Set<ColumnParameter> = columnsMethodParameter
        ?.hashKeysOrEntriesOfType<StringLiteralExpression>()
        ?.map { ColumnParameter(StringParameter(it)) }
        ?.toSet() ?: setOf()

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (parameter.parentMethodParameter != columnsMethodParameter) return listOf()

        return this.queryStatement.query().model?.table?.columns()?.map { it.asLookupElement() }?.toList() ?: listOf()
    }
}
