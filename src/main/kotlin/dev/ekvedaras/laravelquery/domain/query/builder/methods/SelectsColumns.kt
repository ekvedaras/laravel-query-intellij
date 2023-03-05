package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter

sealed interface SelectsColumns : QueryMethodCall, ReferencesColumn, ReferencesTable {
    val columns: Set<ColumnParameter>

    private fun columnParameterFor(stringParameter: StringParameter): ColumnParameter? =
        columns.find { stringParameter.equals(it) }

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        columnParameterFor(parameter)?.getCompletionOptions(queryStatement.query) ?: listOf()

    override fun findColumnReferencedIn(parameter: StringParameter): DbColumn? =
        columnParameterFor(parameter)?.findColumnReference(queryStatement.query)

    override fun findTableReferencedIn(parameter: StringParameter): DbTable? =
        columnParameterFor(parameter)?.findTableReference(queryStatement.query)

    override fun findNamespaceReferencedIn(parameter: StringParameter): DbNamespace? =
        columnParameterFor(parameter)?.findNamespaceReference(queryStatement.query)
}
