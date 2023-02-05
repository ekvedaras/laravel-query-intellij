package dev.ekvedaras.laravelquery.domain.query.queryVariable

import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.model.Model.Companion.isWithinModelScopeMethod
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.isChildOfAny
import dev.ekvedaras.laravelquery.support.isFirstParameter
import dev.ekvedaras.laravelquery.support.transform

data class ModelScopeQueryVariable(override val variable: Variable) : QueryVariable {
    init {
        if (!clazz.isChildOfAny(LaravelClasses.EloquentBuilder, orIsAny = true)) {
            throw Exception("Variable ${variable.name} is not an eloquent builder variable but an instance of ${clazz.fqn}")
        }

        if (!variable.isWithinModelScopeMethod()) throw Exception("Variable ${variable.name} is not within model scope method")

        if (!variable.isFirstParameter()) throw Exception("Variable ${variable.name} is not the first parameter of parent closure")
    }

    override val model: Model? = variable.parentOfType<PhpClass>().transform { clazz -> Model(clazz) }
}
