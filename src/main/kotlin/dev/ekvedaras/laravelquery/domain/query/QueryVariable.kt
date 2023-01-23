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
import dev.ekvedaras.laravelquery.domain.model.Model.Companion.isModelScopeQuery
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.cleanup
import dev.ekvedaras.laravelquery.support.isChildOfAny
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.tryTransforming

data class QueryVariable(var variable: Variable, val query: Query) {
    private val clazz = if (DumbService.isDumb(variable.project)) throw Exception("Cannot instantiate query variables while php index is building")
    else PhpIndex.getInstance(variable.project)
        .completeType(variable.project, variable.type, mutableSetOf())
        .types
        .flatMap { PhpIndex.getInstance(variable.project).getClassesByFQN(it) }
        .firstOrNull() ?: throw Exception("Cannot find a class of query variable")

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

    fun isJoinClause(): Boolean = clazz.isChildOfAny(LaravelClasses.JoinClause, orIsAny = true)
    fun isRelationClause(): Boolean = clazz.isChildOfAny(LaravelClasses.Relation, orIsAny = true)

    val model: Model? =
        when {
            isModelScopeQuery() -> transform {
                it.variable.parentOfType<PhpClass>().transform { clazz -> Model(clazz) }
            }

            isRelationClause() ->
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
                            .tryTransforming { QueryStatement(it, query) }
                            ?.model
                            ?.relation(relationName)
                    }

            else -> null
        }
}
