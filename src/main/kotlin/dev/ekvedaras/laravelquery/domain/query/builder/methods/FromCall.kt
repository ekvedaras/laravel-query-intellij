package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.Alias
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.Table
import kotlin.streams.toList
import dev.ekvedaras.laravelquery.domain.database.Table as DbTable

class FromCall(override val reference: MethodReference, override val queryStatement: QueryStatement): MethodCall {
    private val tableParameter = reference.getParameter(0)
    private val aliasParameter = reference.getParameter(1)

    val table: Table? = when (this.tableParameter) {
        is StringLiteralExpression -> Table(this.tableParameter)
        else -> null
    }

    val alias: Alias? = when (this.aliasParameter) {
        is StringLiteralExpression -> Alias(this.aliasParameter)
        else -> null
    }

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        val completion = mutableListOf<LookupElement>()

        if (parameter.isEmpty) {
            completion += Namespace.list(reference.project).map { it.asLookupElement() }.toList()
            completion += DbTable.list(reference.project).map { it.asLookupElement() }.toList()
        } else if (parameter.hasTwoParts && parameter.hasUncompletedPart) {
            completion += queryStatement.query()
                .dataSource
                ?.findNamespace(parameter.parts.first())
                ?.tables()
                ?.map { it.asLookupElement() }
                ?.toList() ?: return completion
        }

        return completion
    }
}
