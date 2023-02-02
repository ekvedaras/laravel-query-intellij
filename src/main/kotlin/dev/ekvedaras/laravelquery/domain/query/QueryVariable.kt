package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.project.DumbService
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.model.Model.Companion.isInsideModelScope
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.cleanup
import dev.ekvedaras.laravelquery.support.isChildOfAny
import dev.ekvedaras.laravelquery.support.isFirstParameter
import dev.ekvedaras.laravelquery.support.isInsideCallOfMethod
import dev.ekvedaras.laravelquery.support.referenceVariable
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.tryTransforming

/**
 * A wrapper data class for a variable that is used to build and store the query.
 * For example `$query = DB::table('users');` or `$query->where('users.id', 1)->get();` - in both cases $query is a query variable.
 *
 * If we have a statement with a query variable, we can ask this variable to give us all the other statements
 * where this variable is used. This way we can build the full query that is split across different statements.
 *
 * In some cases the variable comes from function parameters like in `DB::table('users)->join('customers', function (JoinClause $join) { ... });`
 * In the above case `$join` would also be considered a query variable.
 *
 * Furthermore, `$query` in model scope functions is also a query variable.
 *
 * For scopes and relation clauses (in `with(['relation' => fn ($relation) => $relation])`) query variable can tell us
 * to which model it is related.
 */
data class QueryVariable(var variable: Variable, val queryStatement: QueryStatement) {
    private val clazz = if (DumbService.isDumb(variable.project)) throw Exception("Cannot instantiate query variables while php index is building")
    else PhpIndex.getInstance(variable.project)
        .completeType(variable.project, variable.type, mutableSetOf())
        .types
        .flatMap { PhpIndex.getInstance(variable.project).getClassesByFQN(it) }
        .firstOrNull() ?: throw Exception("Cannot find a class of query variable")

    companion object {
        fun from(statement: QueryStatement): QueryVariable? = statement
            .statement
            .referenceVariable()
            .tryTransforming { QueryVariable(it, statement) }
    }

    init {
        if (!clazz.isChildOfAny(
                LaravelClasses.QueryBuilder,
                LaravelClasses.EloquentBuilder,
                LaravelClasses.DbFacadeAlias,
                LaravelClasses.DbFacade,
                LaravelClasses.JoinClause,
                LaravelClasses.Model,
                LaravelClasses.Relation,
                orIsAny = true,
            )) throw Exception("Variable ${variable.name} is not a query variable but an instance of ${clazz.fqn}")
    }

    fun usageStatements(): List<Statement> =
        if (DumbService.isDumb(variable.project))
            listOf()
        else ReferencesSearch.search(variable.originalElement, variable.resolveScope, false)
            .toList()
            .filterNot { it.element.originalElement == variable.originalElement }
            .mapNotNull { it.element.parentOfType() }

    fun isInsideJoinClause(): Boolean =
        clazz.isChildOfAny(LaravelClasses.JoinClause, orIsAny = true)

    fun isInsideWhereClause(): Boolean =
        clazz.isChildOfAny(LaravelClasses.QueryBuilder, LaravelClasses.EloquentBuilder, orIsAny = true)
            && variable.isInsideCallOfMethod("where")
            && variable.isFirstParameter()

    fun isInsideWhenClause(): Boolean =
        clazz.isChildOfAny(LaravelClasses.QueryBuilder, LaravelClasses.EloquentBuilder, orIsAny = true)
            && variable.isInsideCallOfMethod("when")
            && variable.isFirstParameter()

    private fun isInsideRelationClause(): Boolean = clazz.isChildOfAny(LaravelClasses.Relation, orIsAny = true)

    val model: Model? =
        when {
            isInsideModelScope() -> transform {
                it.variable.parentOfType<PhpClass>().transform { clazz -> Model(clazz) }
            }

            isInsideRelationClause() ->
                variable
                    .parentOfType<Function>()
                    ?.parentOfType<ArrayHashElement>()
                    ?.key
                    ?.text
                    ?.cleanup()
                    ?.tryTransforming { relationName ->
                        variable
                            .parentOfType<Function>()
                            ?.parentOfType<Statement>()
                            .tryTransforming { QueryStatement.from(it) }
                            ?.model
                            ?.relation(relationName)
                    }

            else -> null
        }
}
