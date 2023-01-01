package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.Key
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.query.builder.methods.MethodCall
import dev.ekvedaras.laravelquery.domain.query.model.Model
import dev.ekvedaras.laravelquery.support.descendantsOfType
import dev.ekvedaras.laravelquery.utils.getClass

val queryKey = Key<Query>("query")

class QueryStatement(val statement: Statement, query: Query? = null) {
    init {
        if (query != null) statement.putUserData(queryKey, query)
    }

    private val firstPsiChild = statement.firstPsiChild
    private val secondPsiDescendant = firstPsiChild?.firstPsiChild

    val callChain: Set<MethodCall> = if (firstPsiChild is AssignmentExpression) {
        firstPsiChild
            .descendantsOfType<MethodReference>()
            .mapNotNull { MethodCall.from(reference = it, queryStatement = this) }
            .toSet()
    } else {
        statement.descendantsOfType<MethodReference>()
            .mapNotNull { MethodCall.from(reference = it, queryStatement = this) }
            .toSet()
    }

    fun query(): Query = statement.getUserData(queryKey)
        ?: Query().apply { statement.putUserData(queryKey, this) }

    val isIncompleteQuery = secondPsiDescendant is Variable
    val queryVariable: QueryVariable? =
        if (this.isIncompleteQuery) QueryVariable(
            variable = secondPsiDescendant as Variable,
            query = query()
        ) else if (secondPsiDescendant is AssignmentExpression && secondPsiDescendant.firstPsiChild is Variable) QueryVariable(
            variable = secondPsiDescendant.firstPsiChild as Variable,
            query = query()
        ) else null

    private val classReference: ClassReference? = this.callChain
        .firstOrNull { it.classReference != null }
        ?.classReference

    val model: Model? =
        if (DumbService.isDumb(statement.project)) null
        else if (this.classReference != null) classReference.getClass(statement.project).run {
            if (this == null) null
            else Model(this)
        }
        else null

    init {
        this.query().addStatement(this)
    }

    fun methodCallFor(methodReference: MethodReference): MethodCall? =
        this.callChain.firstOrNull { it.reference == methodReference }
}
