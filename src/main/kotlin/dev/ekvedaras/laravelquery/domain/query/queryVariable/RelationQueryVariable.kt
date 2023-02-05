package dev.ekvedaras.laravelquery.domain.query.queryVariable

import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.cleanup
import dev.ekvedaras.laravelquery.support.isChildOfAny
import dev.ekvedaras.laravelquery.support.tryTransforming

data class RelationQueryVariable(override val variable: Variable) : QueryVariable {
    init {
        if (!clazz.isChildOfAny(LaravelClasses.Relation, orIsAny = true)) {
            throw Exception("Variable ${variable.name} is not a Relation query variable but an instance of ${clazz.fqn}")
        }
    }

    override val model: Model? = variable
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
}
