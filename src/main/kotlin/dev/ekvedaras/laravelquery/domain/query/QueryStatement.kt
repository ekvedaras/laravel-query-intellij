package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.util.Key
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.model.Model.Companion.isModelScopeQuery
import dev.ekvedaras.laravelquery.support.descendantsOfType
import dev.ekvedaras.laravelquery.support.tap
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.tryTransforming

private val queryKey = Key<Query>("query")

class QueryStatement(val statement: Statement, query: Query? = null) {
    init {
        query.tap { statement.putUserData(queryKey, it) }
    }

    fun query(): Query = statement.getUserData(queryKey)
        ?: Query().apply { statement.putUserData(queryKey, this) }

    private val firstPsiChild = statement.firstPsiChild
    private val lastMethodReference = firstPsiChild?.descendantsOfType<MethodReference>()?.lastOrNull().run {
        if (this is MethodReference) this
        else if (firstPsiChild is MethodReference) firstPsiChild
        else null
    }

    val isIncompleteQuery = lastMethodReference?.firstPsiChild is Variable
    val queryVariable: QueryVariable? =
        if (this.isIncompleteQuery) lastMethodReference?.firstPsiChild.tryTransforming {
            QueryVariable(
                variable = it as Variable,
                query = query()
            )
        } else if (lastMethodReference?.firstPsiChild is AssignmentExpression && lastMethodReference.firstPsiChild?.firstPsiChild is Variable) lastMethodReference.firstPsiChild?.firstPsiChild.tryTransforming {
            QueryVariable(
                variable = it as Variable,
                query = query()
            )
        } else null

    val callChain: QueryStatementElementCallChain = QueryStatementElementCallChain.collect(
        startingFrom = firstPsiChild,
        forStatement = this,
    )

    val model: Model? =
        when {
            queryVariable?.isModelScopeQuery() == true -> queryVariable.transform {
                it.variable.parentOfType<PhpClass>().transform { clazz -> Model(clazz) }
            }

            queryVariable?.isRelationClause() == true -> queryVariable.transform {
                TODO("Load parent query model (see below), get relation name from array key and request relation model from parent model")
            }

            else -> this.callChain.firstClassReference.transform { Model.from(it) }
        }

    init {
        this.query().addStatement(this)
    }
}
