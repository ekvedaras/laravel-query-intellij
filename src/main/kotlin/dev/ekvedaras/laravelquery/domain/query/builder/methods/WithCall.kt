package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.RelationParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrFirstEntryOfType
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class WithCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, SelectsRelations {
    private val relationParameter = reference.getParameter(0)
    private val closureParameter = reference.getParameter(1) as? Function

    override val relations: Set<RelationParameter> = when {
        relationParameter is ArrayCreationExpression || closureParameter != null -> reference.getParameter(0).transformInstanceOf<ArrayCreationExpression, Set<RelationParameter>> { array ->
            array
                .hashKeysOrFirstEntryOfType<StringLiteralExpression>()
                .map { RelationParameter(it.asStringParameter()) }
                .toSet()
        } ?: setOf()
        else -> reference.parameters
            .filterIsInstance<StringLiteralExpression>()
            .map { RelationParameter(it.asStringParameter()) }
            .toSet()
    }
}
