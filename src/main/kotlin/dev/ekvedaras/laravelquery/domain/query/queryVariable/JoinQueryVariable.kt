package dev.ekvedaras.laravelquery.domain.query.queryVariable

import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.isChildOfAny

data class JoinQueryVariable(override val variable: Variable) : QueryVariable, InterestedInUpperScope {
    init {
        if (!clazz.isChildOfAny(LaravelClasses.JoinClause, orIsAny = true)) {
            throw Exception("Variable ${variable.name} is not a JoinClause query variable but an instance of ${clazz.fqn}")
        }
    }
}
