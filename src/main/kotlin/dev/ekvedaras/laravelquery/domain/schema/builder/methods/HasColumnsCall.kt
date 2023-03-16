package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.intellij.database.psi.DbTable
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.ReferencesColumn
import dev.ekvedaras.laravelquery.domain.ReferencesTable
import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter
import dev.ekvedaras.laravelquery.domain.StandaloneTableParameter
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.schema.Migration
import dev.ekvedaras.laravelquery.support.nonHashEntriesOfType
import dev.ekvedaras.laravelquery.support.returnWhen
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class HasColumnsCall(override val reference: MethodReference, override val migration: Migration) : SchemaBuilderMethodCall, ReferencesTable, ReferencesColumn {
    val tableParameter = reference.getParameter(0).transformInstanceOf<StringLiteralExpression, StandaloneTableParameter> {
        StandaloneTableParameter(it.asStringParameter())
    }

    override fun findTableReferencedIn(parameter: StringParameter): DbTable? = returnWhen(parameter.equals(tableParameter), tableParameter?.table?.asDbTable())


    private val columnsParameter = reference.getParameter(1) as? ArrayCreationExpression

    private val columns = columnsParameter.transform { array ->
        array
            .nonHashEntriesOfType<StringLiteralExpression>()
            .map { StandaloneColumnParameter(it.asStringParameter()) }
    }

    override fun findColumnReferencedIn(parameter: StringParameter): DbColumn? = tableParameter?.table.transform { table ->
        columns?.find { parameter.equals(it) }?.findColumnReference(table)
    }

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        returnWhen(parameter.equals(tableParameter)) { tableParameter?.getCompletionOptions() }
            ?: tableParameter?.table.transform { table ->
                columns?.find { parameter.equals(it) }?.getCompletionOptions(table)
            } ?: listOf()
}
