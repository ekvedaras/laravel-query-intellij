package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.Column
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.StringParameter
import kotlin.streams.toList

class SelectCall(override val reference: MethodReference, override val queryStatement: QueryStatement) : MethodCall {
    private val columnsParameter = reference.getParameter(0)

    val columns: Set<Column> = when (this.columnsParameter) {
        is ArrayCreationExpression -> {
            this.columnsParameter.childrenOfType<StringLiteralExpression>().map { Column(StringParameter(it)) }.toSet()
        }

        is StringLiteralExpression -> {
            setOf(Column(StringParameter(this.columnsParameter)))
        }

        else -> {
            setOf()
        }
    }

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        val completion = mutableListOf<LookupElement>()

        if (parameter.isEmpty) {
            completion += queryStatement.query().namespaces.map { it.asLookupElement() }.toList()
            completion += queryStatement.query().tables.map { it.asLookupElement() }.toList()
            completion += queryStatement.query().tables
                .flatMap { table -> table.columns().map { it.asLookupElement() }.toList() }
                .toList()
        }

        return completion
    }
}
