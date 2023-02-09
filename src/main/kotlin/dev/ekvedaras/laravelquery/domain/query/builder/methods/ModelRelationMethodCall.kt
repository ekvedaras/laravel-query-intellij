package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.support.classReference

class ModelRelationMethodCall(val model: Model, override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall {
    override val classReference: ClassReference? = reference.classReference()
    override fun completeFor(parameter: StringParameter): List<LookupElement> = listOf()
}
