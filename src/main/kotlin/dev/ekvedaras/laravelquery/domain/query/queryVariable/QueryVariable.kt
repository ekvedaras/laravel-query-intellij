package dev.ekvedaras.laravelquery.domain.query.queryVariable

import com.intellij.openapi.project.DumbService
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.support.referenceVariable
import dev.ekvedaras.laravelquery.support.tryTransformingInstanceOfOrContinue
import dev.ekvedaras.laravelquery.support.tryTransformingInstanceOfUnless

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
interface QueryVariable {
    val variable: Variable
    val clazz: PhpClass
        get() = if (DumbService.isDumb(variable.project)) throw Exception("Cannot instantiate query variables while php index is building")
        else PhpIndex.getInstance(variable.project)
            .completeType(variable.project, variable.type, mutableSetOf())
            .types
            .flatMap { PhpIndex.getInstance(variable.project).getClassesByFQN(it) }
            .firstOrNull() ?: throw Exception("Cannot find a class of query variable")
    val model: Model? get() = null

    fun usageStatements(): List<Statement> =
        if (DumbService.isDumb(variable.project))
            listOf()
        else ReferencesSearch.search(variable.originalElement, variable.resolveScope, false)
            .toList()
            .filterNot { it.element.originalElement == variable.originalElement }
            .mapNotNull { it.element.parentOfType() }

    companion object {
        fun from(statement: QueryStatement): QueryVariable? = statement
            .statement
            .referenceVariable()
            .tryTransformingInstanceOfOrContinue<Variable, WhereQueryVariable> { WhereQueryVariable(it) }
            .tryTransformingInstanceOfOrContinue<Variable, WhenQueryVariable> { WhenQueryVariable(it) }
            .tryTransformingInstanceOfOrContinue<Variable, JoinQueryVariable> { JoinQueryVariable(it) }
            .tryTransformingInstanceOfOrContinue<Variable, RelationQueryVariable> { RelationQueryVariable(it) }
            .tryTransformingInstanceOfOrContinue<Variable, ModelScopeQueryVariable> { ModelScopeQueryVariable(it) }
            .tryTransformingInstanceOfUnless<QueryVariable, Variable, DynamicQueryVariable> { DynamicQueryVariable(it) }
    }
}
