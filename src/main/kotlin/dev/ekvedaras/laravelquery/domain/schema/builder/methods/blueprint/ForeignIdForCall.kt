package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.schema.MigrationTable
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class ForeignIdForCall(override val reference: MethodReference, override val table: MigrationTable) : BlueprintMethodCall, MigratesColumns {
    override val columns = listOfNotNull(
        reference.getParameter(1).transformInstanceOf<StringLiteralExpression, StandaloneColumnParameter> {
            StandaloneColumnParameter(it.asStringParameter())
        } // TODO Resolve from reference.getParameter(0) class string or constant if column name is not provided
    )

    override fun findColumnReferencedIn(parameter: StringParameter): DbColumn? =
        table.asExistingTable().transform { existingTable ->
            columns.find { parameter.equals(it) }.transform {
                it.findColumnReference(existingTable)
            }
        }

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        migration.migratedColumns(forTable = table).map { it.asLookupElement(table) } + (table.asExistingTable().transform { existingTable ->
            columns.find { parameter.equals(it) }?.getCompletionOptions(existingTable)
        } ?: listOf())
}
