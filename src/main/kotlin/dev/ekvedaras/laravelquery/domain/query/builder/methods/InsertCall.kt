package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrEntriesOfType
import dev.ekvedaras.laravelquery.support.hashKeysOrFirstEntryOfType
import dev.ekvedaras.laravelquery.support.nonHashEntriesOfType
import dev.ekvedaras.laravelquery.support.transform

class InsertCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val columnsMethodParameter = reference.getParameter(0) as? ArrayCreationExpression

    override val columns: Set<ColumnParameter> = columnsMethodParameter.transform { array ->
        array
            .hashKeysOrEntriesOfType<StringLiteralExpression>()
            .map { ColumnParameter(it.asStringParameter()) }
            .toSet() +
            array
                .nonHashEntriesOfType<ArrayCreationExpression>()
                .flatMap { innerArray ->
                    innerArray
                        .hashKeysOrFirstEntryOfType<StringLiteralExpression>()
                        .map { ColumnParameter(it.asStringParameter()) }
                }.toSet()
    } ?: setOf()
}
