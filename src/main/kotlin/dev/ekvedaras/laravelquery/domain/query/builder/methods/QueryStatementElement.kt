package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import dev.ekvedaras.laravelquery.domain.query.QueryStatement

interface QueryStatementElement {
    val reference: PhpPsiElement
    val queryStatement: QueryStatement
    val classReference: ClassReference?
}
