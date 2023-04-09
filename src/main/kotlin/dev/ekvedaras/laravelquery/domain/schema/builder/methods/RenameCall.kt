package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbTable
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.ReferencesTable
import dev.ekvedaras.laravelquery.domain.StandaloneTableParameter
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.schema.Migration
import dev.ekvedaras.laravelquery.support.returnWhen
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class RenameCall(override val reference: MethodReference, override val migration: Migration) : SchemaBuilderMethodCall, MigratesTable {
    private val fromTableParameter = reference.getParameter(0).transformInstanceOf<StringLiteralExpression, StandaloneTableParameter> {
        StandaloneTableParameter(it.asStringParameter())
    }

    private val toTableParameter = reference.getParameter(1).transformInstanceOf<StringLiteralExpression, StandaloneTableParameter> {
        StandaloneTableParameter(it.asStringParameter())
    }

    override val tableParameter: StandaloneTableParameter? = toTableParameter

    override fun findTableReferencedIn(parameter: StringParameter): DbTable? =
        returnWhen(parameter.equals(fromTableParameter), fromTableParameter?.table?.asDbTable())
            ?: returnWhen(parameter.equals(toTableParameter), toTableParameter?.table?.asDbTable())


    override fun completeFor(parameter: StringParameter): List<LookupElement> = returnWhen(parameter.equals(fromTableParameter)) {
        migration.tables.map { it.asLookupElement() } + (fromTableParameter?.getCompletionOptions() ?: listOf())
    } ?: returnWhen(parameter.equals(toTableParameter)) {
        migration.tables.map { it.asLookupElement() } + (toTableParameter?.getCompletionOptions() ?: listOf())
    } ?: listOf()
}
