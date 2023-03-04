package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.RelationParameter
import dev.ekvedaras.laravelquery.support.hashKeysOrFirstEntryOfType
import dev.ekvedaras.laravelquery.support.nonHashEntriesOfType
import dev.ekvedaras.laravelquery.support.transformInstanceOf
import dev.ekvedaras.laravelquery.support.tryTransformingInstanceOfOrContinue
import dev.ekvedaras.laravelquery.support.tryTransformingInstanceOfUnless

class WithoutCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, RelationSelectionCall {
    override val relations: Set<RelationParameter> = reference.getParameter(0).tryTransformingInstanceOfOrContinue<StringLiteralExpression, Set<RelationParameter>> {
        reference.parameters.filterIsInstance<StringLiteralExpression>().map { RelationParameter(it.asStringParameter()) }.toSet()
    }.tryTransformingInstanceOfUnless<Set<RelationParameter>, ArrayCreationExpression, Set<RelationParameter>> { array ->
        array
            .nonHashEntriesOfType<StringLiteralExpression>()
            .map { RelationParameter(it.asStringParameter()) }
            .toSet()
    } ?: setOf()
}
