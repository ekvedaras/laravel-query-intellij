package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.RelationKeyParameter
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf
import dev.ekvedaras.laravelquery.support.tryTransforming

class HasManyCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, DefinesModelRelation {
    // TODO queryVariable is not yet defined at this point. Need to pass it in completeMethod/findReference method or pass a resolved closure
    override val columns: Set<RelationKeyParameter> = setOf(
        reference.getParameter(1).transformInstanceOf<StringLiteralExpression, RelationKeyParameter> { RelationKeyParameter(it.asStringParameter(), queryStatement.queryVariable?.model) },
        reference.getParameter(2).transformInstanceOf<StringLiteralExpression, RelationKeyParameter> { RelationKeyParameter(
            it.asStringParameter(),
            reference.parentOfType<PhpClass>().tryTransforming { clazz -> Model(clazz) }
        ) },
    ).filterNotNull().toSet()
}
