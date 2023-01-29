package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import dev.ekvedaras.laravelquery.domain.query.QueryStatement

/**
 * Represents any meaningful PSI element in query statements.
 * For example new Model() is considered a query statement element but is not a method call.
 *
 * @see QueryMethodCall
 */
sealed interface QueryStatementElement {
    val reference: PhpPsiElement
    val queryStatement: QueryStatement
    val classReference: ClassReference?
}
