package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.openapi.project.DumbService
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.model.Model
import dev.ekvedaras.laravelquery.utils.getClass

class NewModelExpression(override val reference: NewExpression, override val queryStatement: QueryStatement) : QueryStatementElement {
    override val classReference: ClassReference? = reference.classReference
    val model: Model? =
        if (DumbService.isDumb(reference.project)) null
        else reference.getClass(reference.project).run {
            if (this == null) null
            else Model(this)
        }
}
