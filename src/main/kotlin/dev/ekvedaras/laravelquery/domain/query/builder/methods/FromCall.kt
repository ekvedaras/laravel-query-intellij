package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.Alias
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.Table

class FromCall(val method: MethodReference): MethodCall {
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
