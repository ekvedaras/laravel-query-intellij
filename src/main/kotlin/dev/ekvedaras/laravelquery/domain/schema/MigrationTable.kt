package dev.ekvedaras.laravelquery.domain.schema

import com.intellij.codeInsight.lookup.LookupElementBuilder
import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.HasBlueprintClosure
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.MigratesTable
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.v4.utils.LookupUtils.Companion.withInsertHandler
import icons.DatabaseIcons

data class MigrationTable(val methodCall: MigratesTable) {
    val name = methodCall.tableParameter?.table?.name
    val project = methodCall.reference.project
    fun asExistingTable(): Table? = name.transform { Table.findFirst(it, project) }

    fun asLookupElement() = methodCall.tableParameter?.asLookupElement()
        ?: LookupElementBuilder
            .create(name ?: methodCall.reference)
            .withIcon(DatabaseIcons.Table)
            .withInsertHandler(project, triggerCompletion = false)

    val columns: List<StandaloneColumnParameter>
        get() = when (methodCall) {
            is HasBlueprintClosure -> methodCall.closure?.columns
            else -> null
        } ?: listOf()
}
