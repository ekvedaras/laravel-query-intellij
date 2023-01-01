package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import dev.ekvedaras.laravelquery.domain.query.QueryStatement

class NewModelExpression(override val reference: NewExpression, override val queryStatement: QueryStatement) : QueryStatementElement {
    override val classReference: ClassReference? = reference.classReference
}
