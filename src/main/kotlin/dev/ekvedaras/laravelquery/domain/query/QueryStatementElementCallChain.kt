package dev.ekvedaras.laravelquery.domain.query

import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import dev.ekvedaras.laravelquery.domain.query.builder.methods.NewModelExpression
import dev.ekvedaras.laravelquery.domain.query.builder.methods.QueryMethodCall
import dev.ekvedaras.laravelquery.domain.query.builder.methods.QueryStatementElement
import dev.ekvedaras.laravelquery.support.callChainOfType
import dev.ekvedaras.laravelquery.support.firstChildOfType

data class QueryStatementElementCallChain(val elements: Set<QueryStatementElement>) {
    companion object {
        fun collect(startingFrom: PsiElement?, forStatement: QueryStatement): QueryStatementElementCallChain {
            if (startingFrom is AssignmentExpression && startingFrom.childrenOfType<NewExpression>().isNotEmpty()) {
                val newExpression = startingFrom.childrenOfType<NewExpression>().first()
                return QueryStatementElementCallChain(
                    setOf(NewModelExpression(reference = newExpression, queryStatement = forStatement))
                )
            }

            if (startingFrom is AssignmentExpression) {
                return QueryStatementElementCallChain(
                    startingFrom
                        .firstChildOfType<MethodReference>()
                        ?.callChainOfType<MethodReference>()
                        ?.mapNotNull { QueryMethodCall.from(reference = it, queryStatement = forStatement) }
                        ?.toSet() ?: setOf()
                )
            }

            return QueryStatementElementCallChain(
                forStatement.statement
                    .callChainOfType<MethodReference>()
                    .mapNotNull { QueryMethodCall.from(reference = it, queryStatement = forStatement) }
                    .toSet()
            )
        }
    }

    val firstClassReference: ClassReference? =
        elements
            .firstOrNull { it.classReference != null }
            ?.classReference

    fun methodCallFor(methodReference: MethodReference): QueryMethodCall? =
        elements.filterIsInstance<QueryMethodCall>().firstOrNull { it.reference == methodReference }

    inline fun forEach(action: (QueryStatementElement) -> Unit) {
        for (element in elements) action(element)
    }
}
