package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.intellij.database.psi.DbTable
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.ReferencesColumn
import dev.ekvedaras.laravelquery.domain.ReferencesTable
import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter
import dev.ekvedaras.laravelquery.domain.StandaloneTableParameter
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.schema.Migration
import dev.ekvedaras.laravelquery.support.returnWhen
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class HasColumnCall(override val reference: MethodReference, override val migration: Migration) : SchemaBuilderMethodCall, ReferencesTable, ReferencesColumn {
    val tableParameter = reference.getParameter(0).transformInstanceOf<StringLiteralExpression, StandaloneTableParameter> {
        StandaloneTableParameter(it.asStringParameter())
    }

    override fun findTableReferencedIn(parameter: StringParameter): DbTable? = returnWhen(parameter.equals(tableParameter), tableParameter?.table?.asDbTable())


    val columnParameter = reference.getParameter(1).transformInstanceOf<StringLiteralExpression, StandaloneColumnParameter> {
        StandaloneColumnParameter(it.asStringParameter())
    }

    override fun findColumnReferencedIn(parameter: StringParameter): DbColumn? = returnWhen(parameter.equals(columnParameter)) {
        tableParameter?.table.transform { columnParameter?.findColumnReference(it) }
    }

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        returnWhen(parameter.equals(tableParameter)) { tableParameter?.getCompletionOptions() }
        ?: returnWhen(parameter.equals(columnParameter)) { tableParameter?.table.transform { columnParameter?.getCompletionOptions(it) } }
        ?: listOf()
}
