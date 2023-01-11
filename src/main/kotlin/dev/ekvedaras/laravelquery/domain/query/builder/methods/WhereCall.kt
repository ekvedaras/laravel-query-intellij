package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.support.elementsOfType
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class WhereCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : QueryMethodCall, ColumnSelectionCall {
    private val columnsMethodParameter = reference.getParameter(0)

    override val columns: Set<ColumnParameter> = when (this.columnsMethodParameter) {
        is ArrayCreationExpression -> {
            this.columnsMethodParameter
                .childrenOfType<ArrayHashElement>()
                .mapNotNull { hashEntry ->
                    hashEntry.key.transformInstanceOf<StringLiteralExpression, ColumnParameter> {
                        ColumnParameter(StringParameter(it))
                    }
                }
                .toSet()
        }

        is StringLiteralExpression -> {
            reference.parameters.filterIsInstance<StringLiteralExpression>().map { ColumnParameter(StringParameter(it)) }.toSet()
        }

        else -> {
            setOf()
        }
    }
}
