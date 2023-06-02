package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.intellij.database.psi.DbIndex
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

class IndexCall(override val reference: MethodReference, override val table: MigrationTable) : BlueprintMethodCall, MigratesIndexes, ReferencesColumn {
    private val columnsParameter = reference.getParameter(0)
    private val nameParameter = reference.getParameter(1) as? StringLiteralExpression

    private val columns = when (columnsParameter) {
        is StringLiteralExpression -> listOf(StandaloneColumnParameter(columnsParameter.asStringParameter()))
        is ArrayCreationExpression -> columnsParameter.nonHashEntriesOfType<StringLiteralExpression>().map {
            StandaloneColumnParameter(it.asStringParameter())
        }
        else -> listOf()
    }

    override val indexes = listOfNotNull(
        nameParameter?.asStringParameter().transform {
            if (it.isEmpty) ComputedNameIndexParameter(table, *columns.toTypedArray()) else NamedIndexParameter(it)
        } ?: ComputedNameIndexParameter(table, *columns.toTypedArray())
    )

    override fun findIndexReferencedIn(parameter: StringParameter): DbIndex? =
        table.asExistingTable().transform { existingTable ->
            indexes.find { parameter.equals(it) }.transform {
                it.findIndexReference(existingTable)
            }
        }

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (columns.find { parameter.equals(it) } != null) {
            return migration.migratedColumns(forTable = table).map { it.asLookupElement(table) } + (table.asExistingTable().transform { existingTable ->
                columns.find { parameter.equals(it) }?.getCompletionOptions(existingTable)
            } ?: listOf())
        }

        return migration.migratedIndexes(forTable = table).map { it.asLookupElement(table) } + (table.asExistingTable().transform { existingTable ->
            existingTable.indexes().map { it.asLookupElement() }.toList()
        } ?: listOf())
    }

    override fun findColumnReferencedIn(parameter: StringParameter): DbColumn? =
        table.asExistingTable().transform { existingTable ->
            columns
                .find { parameter.equals(it) }
                ?.findColumnReference(existingTable)
        }
}
