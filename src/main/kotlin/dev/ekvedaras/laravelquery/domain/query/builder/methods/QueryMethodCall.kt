package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.classReference
import dev.ekvedaras.laravelquery.support.isMemberOfAny

sealed interface QueryMethodCall : QueryStatementElement {
    override val reference: MethodReference
    override val classReference: ClassReference?
        get() = this.reference.classReference()

    fun completeFor(parameter: StringParameter): List<LookupElement>

    companion object {
        fun from(reference: MethodReference, queryStatement: QueryStatement): QueryMethodCall? {
            if (!reference.isMemberOfAny(
                    LaravelClasses.QueryBuilder,
                    LaravelClasses.EloquentBuilder,
                    LaravelClasses.DbFacade,
                    LaravelClasses.DbFacadeAlias,
                    LaravelClasses.Model,
                )) return null

            return when (reference.name) {
                "newQuery" -> NewQueryCall(reference, queryStatement)
                "create" -> CreateCall(reference, queryStatement)
                "from" -> FromCall(reference, queryStatement)
                "join" -> JoinCall(reference, queryStatement)
                "where" -> WhereCall(reference, queryStatement)
                "get" -> GetCall(reference, queryStatement)
                "select" -> SelectCall(reference, queryStatement)
                else -> null
            }
        }
    }
}
