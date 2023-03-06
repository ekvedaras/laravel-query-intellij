package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.RelationParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class GetRelationCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, SelectsRelations {
    override val relations: Set<RelationParameter> = reference.getParameter(0).transformInstanceOf<StringLiteralExpression, Set<RelationParameter>> {
        setOf(RelationParameter(it.asStringParameter()))
    } ?: setOf()
}