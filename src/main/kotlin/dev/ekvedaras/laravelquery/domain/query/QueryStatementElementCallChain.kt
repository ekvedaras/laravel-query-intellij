package dev.ekvedaras.laravelquery.domain.query

import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.query.builder.methods.ModelRelationMethodCall
import dev.ekvedaras.laravelquery.domain.query.builder.methods.NewModelExpression
import dev.ekvedaras.laravelquery.domain.query.builder.methods.QueryMethodCall
import dev.ekvedaras.laravelquery.domain.query.builder.methods.QueryStatementElement
import dev.ekvedaras.laravelquery.support.callChainOfType
import dev.ekvedaras.laravelquery.support.firstChildOfType
import dev.ekvedaras.laravelquery.support.transform

data class QueryStatementElementCallChain(val elements: Set<QueryStatementElement>) {
    companion object {
        fun from(statement: QueryStatement): QueryStatementElementCallChain {
            val startingFrom = statement.statement.firstPsiChild

            if (startingFrom is AssignmentExpression && startingFrom.childrenOfType<NewExpression>().isNotEmpty()) {
                val newExpression = startingFrom.childrenOfType<NewExpression>().first()
                return QueryStatementElementCallChain(
                    setOf(NewModelExpression(reference = newExpression, queryStatement = statement))
                )
            }

            return QueryStatementElementCallChain(
                when (statement.statement.firstPsiChild) {
                    is AssignmentExpression -> statement.statement.firstPsiChild
                    else -> statement.statement
                }
                    ?.firstChildOfType<MethodReference>()
                    ?.callChainOfType<MethodReference>()
                    ?.mapNotNull { QueryMethodCall.from(reference = it, queryStatement = statement) }
                    ?.toSet() ?: setOf()
            )
        }
    }

    private val relationModel: Model? = elements.filterIsInstance<ModelRelationMethodCall>().firstOrNull()?.model
    private val firstClassReference: ClassReference? = elements.firstOrNull { it.classReference != null }?.classReference
    val model: Model? = relationModel ?: firstClassReference.transform { Model.from(it) }

    fun methodCallFor(methodReference: MethodReference): QueryMethodCall? =
        elements.filterIsInstance<QueryMethodCall>().firstOrNull { it.reference == methodReference }

    inline fun forEach(action: (QueryStatementElement) -> Unit) {
        for (element in elements) action(element)
    }
}
