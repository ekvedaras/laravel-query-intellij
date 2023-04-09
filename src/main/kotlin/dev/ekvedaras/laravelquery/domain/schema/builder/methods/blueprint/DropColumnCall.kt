package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.ReferencesColumn
import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.schema.MigrationTable
import dev.ekvedaras.laravelquery.support.nonHashEntriesOfType
import dev.ekvedaras.laravelquery.support.transform

class DropColumnCall(override val reference: MethodReference, override val table: MigrationTable) : BlueprintMethodCall, ReferencesColumn {
    private val firstParameter = reference.getParameter(0)
    private val columnParameter = when (firstParameter) {
        is StringLiteralExpression -> listOf(StandaloneColumnParameter(firstParameter.asStringParameter()))
        is ArrayCreationExpression -> firstParameter
            .nonHashEntriesOfType<StringLiteralExpression>()
            .map { StandaloneColumnParameter(it.asStringParameter()) }
        else -> listOf()
    }

    override fun findColumnReferencedIn(parameter: StringParameter): DbColumn? =
        table.asExistingTable().transform { existingTable ->
            columnParameter
                .find { parameter.equals(it) }
                ?.findColumnReference(existingTable)
        }

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        (table.methodCall
            .migration
            .tables
            .filter { table.name == it.name }
            .flatMap { it.columns.map { column -> column.asLookupElement(it) } }
            ) + (table.asExistingTable().transform { existingTable ->
            columnParameter
                .find { parameter.equals(it) }
                ?.getCompletionOptions(existingTable)
        } ?: listOf())
}
