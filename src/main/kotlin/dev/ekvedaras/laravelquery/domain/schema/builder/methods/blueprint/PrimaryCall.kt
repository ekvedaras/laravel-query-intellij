package dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.intellij.database.psi.DbTableKey
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

class PrimaryCall(override val reference: MethodReference, override val table: MigrationTable) : BlueprintMethodCall, MigratesTableKeys, ReferencesColumn {
    private val columnsParameter = reference.getParameter(0)
    private val nameParameter = reference.getParameter(1) as? StringLiteralExpression

    private val columns = when (columnsParameter) {
        is StringLiteralExpression -> listOf(StandaloneColumnParameter(columnsParameter.asStringParameter()))
        is ArrayCreationExpression -> columnsParameter.nonHashEntriesOfType<StringLiteralExpression>().map {
            StandaloneColumnParameter(it.asStringParameter())
        }
        else -> listOf()
    }

    override val tableKeys = listOfNotNull(
        nameParameter?.asStringParameter().transform {
            if (it.isEmpty) ComputedNamePrimaryKeyParameter(table, *columns.toTypedArray()) else NamedTableKeyParameter(it, isPrimary = true)
        } ?: ComputedNamePrimaryKeyParameter(table, *columns.toTypedArray())
    )

    override fun findTableKeyReferencedIn(parameter: StringParameter): DbTableKey? =
        table.asExistingTable().transform { existingTable ->
            tableKeys.find { parameter.equals(it) }.transform {
                it.findTableKeyReference(existingTable)
            }
        }

    override fun completeFor(parameter: StringParameter): List<LookupElement> {
        if (columns.find { parameter.equals(it) } != null) {
            return migration.migratedColumns(forTable = table).map { it.asLookupElement(table) } + (table.asExistingTable().transform { existingTable ->
                columns.find { parameter.equals(it) }?.getCompletionOptions(existingTable)
            } ?: listOf())
        }

        return migration.migratedTablePrimaryKeys(forTable = table).map { it.asLookupElement(table) } + (table.asExistingTable().transform { existingTable ->
            listOfNotNull(existingTable.primaryKey()?.asLookupElement())
        } ?: listOf())
    }

    override fun findColumnReferencedIn(parameter: StringParameter): DbColumn? =
        table.asExistingTable().transform { existingTable ->
            columns
                .find { parameter.equals(it) }
                ?.findColumnReference(existingTable)
        }
}
