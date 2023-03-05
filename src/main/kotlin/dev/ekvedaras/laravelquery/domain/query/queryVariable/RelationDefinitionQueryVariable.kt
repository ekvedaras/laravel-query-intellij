package dev.ekvedaras.laravelquery.domain.query.queryVariable

import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.model.Model.Companion.isWithinModelScopeMethod
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.isChildOfAny
import dev.ekvedaras.laravelquery.support.isFirstParameter
import dev.ekvedaras.laravelquery.support.transform

data class RelationDefinitionQueryVariable(override val variable: Variable) : QueryVariable {
    init {
        if (!clazz.isChildOfAny(LaravelClasses.Model, orIsAny = true)) {
            throw Exception("Variable ${variable.name} is not a model variable but an instance of ${clazz.fqn}")
        }

        if (variable.name != "this") throw Exception("Variable ${variable.name} must be named \$this")
    }

    override val model: Model? = variable.parentOfType<Method>().transform {
        variable.parentOfType<PhpClass>().transform { clazz -> Model(clazz) }?.relation(it.name)
    }
}
