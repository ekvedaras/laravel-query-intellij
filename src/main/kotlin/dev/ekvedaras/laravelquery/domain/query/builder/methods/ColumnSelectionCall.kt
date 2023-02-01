package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.database.psi.DbColumn
import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.ColumnParameter

sealed interface ColumnSelectionCall : QueryMethodCall, ReferencesColumn {
    val columns: Set<ColumnParameter>

    private fun columnParameterFor(stringParameter: StringParameter): ColumnParameter? =
        columns.find { stringParameter.equals(it) }

    override fun completeFor(parameter: StringParameter): List<LookupElement> =
        this.columnParameterFor(parameter)?.getCompletionOptions(queryStatement.query) ?: listOf()

    override fun findColumnReferencedIn(parameter: StringParameter): DbColumn? =
        this.columnParameterFor(parameter)?.findColumnReference(queryStatement.query)

    override fun findTableReferencedIn(parameter: StringParameter): DbTable? =
        this.columnParameterFor(parameter)?.findTableReference(queryStatement.query)

    override fun findNamespaceReferencedIn(parameter: StringParameter): DbNamespace? =
        this.columnParameterFor(parameter)?.findNamespaceReference(queryStatement.query)
}
