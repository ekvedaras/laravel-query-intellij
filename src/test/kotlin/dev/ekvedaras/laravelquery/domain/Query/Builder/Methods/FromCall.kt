package dev.ekvedaras.laravelquery.domain.Query.Builder.Methods

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.Query.Builder.Methods.Parameters.Alias
import dev.ekvedaras.laravelquery.domain.Query.Builder.Methods.Parameters.Table

class FromCall(val method: MethodReference) {
    private val tableParameter = method.getParameter(0)
    private val aliasParameter = method.getParameter(1)

    val table: Table? = when (this.tableParameter) {
        is StringLiteralExpression -> Table(this.tableParameter)
        else -> null
    }

    val alias: Alias? = when (this.aliasParameter) {
        is StringLiteralExpression -> Alias(this.aliasParameter)
        else -> null
    }
}
