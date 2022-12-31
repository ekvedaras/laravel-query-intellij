package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.StringParameter
import dev.ekvedaras.laravelquery.support.elementsOfType

class SelectCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : MethodCall, ColumnSelectionCall {
    private val firstParameter = reference.getParameter(0)

    override val columns: Set<ColumnParameter> = when (this.firstParameter) {
        is ArrayCreationExpression -> {
            this.firstParameter.elementsOfType<StringLiteralExpression>().map { ColumnParameter(StringParameter(it)) }.toSet()
        }

        is StringLiteralExpression -> {
            reference.parameters.filterIsInstance<StringLiteralExpression>().map { ColumnParameter(StringParameter(it)) }.toSet()
        }

        else -> {
            setOf()
        }
    }
}
