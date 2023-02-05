package dev.ekvedaras.laravelquery.domain.query.queryVariable

import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.isChildOfAny
import dev.ekvedaras.laravelquery.support.isFirstParameter
import dev.ekvedaras.laravelquery.support.isInsideCallOfMethod

data class WhenQueryVariable(override val variable: Variable) : QueryVariable, InterestedInUpperScope {
    init {
        if (!clazz.isChildOfAny(LaravelClasses.QueryBuilder, LaravelClasses.EloquentBuilder, orIsAny = true)) {
            throw Exception("Variable ${variable.name} is not a query or eloquent builder variable but an instance of ${clazz.fqn}")
        }

        if (!variable.isInsideCallOfMethod("when")) throw Exception("Variable ${variable.name} is not inside where method");

        if (!variable.isFirstParameter()) throw Exception("Variable ${variable.name} is not the first parameter of parent closure");
    }
}
