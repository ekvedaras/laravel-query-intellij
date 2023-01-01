package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.Key
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.query.builder.methods.MethodCall
import dev.ekvedaras.laravelquery.domain.query.builder.methods.NewModelExpression
import dev.ekvedaras.laravelquery.domain.query.builder.methods.QueryStatementElement
import dev.ekvedaras.laravelquery.domain.query.model.Model
import dev.ekvedaras.laravelquery.support.descendantsOfType
import dev.ekvedaras.laravelquery.utils.getClass

val queryKey = Key<Query>("query")

class QueryStatement(val statement: Statement, query: Query? = null) {
    init {
        if (query != null) statement.putUserData(queryKey, query)
    }

    private val firstPsiChild = statement.firstPsiChild
    private val lastMethodReference = firstPsiChild?.descendantsOfType<MethodReference>()?.lastOrNull().run {
        if (this is MethodReference) this
        else if (firstPsiChild is MethodReference) firstPsiChild
        else null
    }

    val callChain: Set<QueryStatementElement> = if (firstPsiChild is AssignmentExpression && firstPsiChild.childrenOfType<NewExpression>().isNotEmpty()) {
        val newExpression = firstPsiChild.childrenOfType<NewExpression>().first()
        setOf(NewModelExpression(reference = newExpression, queryStatement = this))
    } else if (firstPsiChild is AssignmentExpression) {
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

    val isIncompleteQuery = lastMethodReference?.firstPsiChild is Variable
    val queryVariable: QueryVariable? =
        if (this.isIncompleteQuery) QueryVariable(
            variable = lastMethodReference?.firstPsiChild as Variable,
            query = query()
        ) else if (lastMethodReference?.firstPsiChild is AssignmentExpression && lastMethodReference?.firstPsiChild?.firstPsiChild is Variable) QueryVariable(
            variable = lastMethodReference.firstPsiChild?.firstPsiChild as Variable,
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
        this.callChain.filterIsInstance<MethodCall>().firstOrNull { it.reference == methodReference }
}
