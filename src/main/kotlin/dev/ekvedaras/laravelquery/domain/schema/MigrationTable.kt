package dev.ekvedaras.laravelquery.domain.schema

import com.intellij.codeInsight.lookup.LookupElementBuilder
import dev.ekvedaras.laravelquery.domain.StandaloneColumnParameter
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.HasBlueprintClosure
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.MigratesTable
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint.MigratedIndex
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint.MigratedTableKey
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.blueprint.NamedIndexParameter
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.v4.utils.LookupUtils.Companion.withInsertHandler
import icons.DatabaseIcons

data class MigrationTable(val methodCall: MigratesTable) {
    val name = methodCall.tableParameter?.table?.name
    val project = methodCall.reference.project
    val migration = methodCall.migration
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

    val indexes: List<MigratedIndex>
        get() = when (methodCall) {
            is HasBlueprintClosure -> methodCall.closure?.indexes
            else -> null
        } ?: listOf()

    val tableKeys: List<MigratedTableKey>
        get() = when (methodCall) {
            is HasBlueprintClosure -> methodCall.closure?.tableKeys
            else -> null
        } ?: listOf()
}
