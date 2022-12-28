package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.Column

class GetCall(val method: MethodReference) : MethodCall {
    private val columnsParameter = method.getParameter(0)

    val columns: Set<Column> = when (this.columnsParameter) {
        is ArrayCreationExpression -> {
            this.columnsParameter.childrenOfType<StringLiteralExpression>().map { Column(it) }.toSet()
        }

        is StringLiteralExpression -> {
            setOf(Column(this.columnsParameter))
        }

        else -> {
            setOf()
        }
    }
}
