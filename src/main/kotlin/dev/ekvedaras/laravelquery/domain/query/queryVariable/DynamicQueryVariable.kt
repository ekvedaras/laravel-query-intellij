package dev.ekvedaras.laravelquery.domain.query.queryVariable

import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.isChildOfAny

data class DynamicQueryVariable(override val variable: Variable) : QueryVariable, InterestedInSurroundingScope {
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
}
