package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrEntriesOfType
import dev.ekvedaras.laravelquery.support.transform

class UpdateOrInsertCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, SelectsColumns {
    private val attributesMethodParameter = reference.getParameter(0) as? ArrayCreationExpression
    private val columnsMethodParameter = reference.getParameter(1) as? ArrayCreationExpression

    override val columns: Set<ColumnParameter> =
        (attributesMethodParameter.transform { array ->
            array
                .hashKeysOrEntriesOfType<StringLiteralExpression>()
                .map { ColumnParameter(it.asStringParameter()) }
                .toSet()
        } ?: setOf()) +
            (columnsMethodParameter.transform { array ->
                array
                    .hashKeysOrEntriesOfType<StringLiteralExpression>()
                    .map { ColumnParameter(it.asStringParameter()) }
                    .toSet()
            } ?: setOf())
}
