package dev.ekvedaras.laravelquery.domain.database

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.openapi.project.Project
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.nameWithoutPrefix
import dev.ekvedaras.laravelquery.utils.LookupUtils.Companion.buildLookup
import dev.ekvedaras.laravelquery.utils.LookupUtils.Companion.withInsertHandler
import java.util.stream.Stream

data class Table(val entity: DasTable, val namespace: Namespace) {
    val project = namespace.project
    val name = entity.name
    val nameWithoutPrefix = this.name.substringAfter(LaravelQuerySettings.getInstance(project).tablePrefix)

    companion object {
        fun list(project: Project): Stream<out Table> =
            DataSource.list(project)
                .flatMap { it.namespaces() }
                .flatMap { it.tables() }

        fun findFirst(table: String, project: Project): Table? =
            DataSource.list(project)
                .firstWhereOrNull { it.findFirstTable(table) != null }
                ?.findFirstTable(table)

        fun isPrefixed(table: DasTable, project: Project) =
            table.name.startsWith(LaravelQuerySettings.getInstance(project).tablePrefix)
    }

    fun columns(): Stream<out Column> =
        this.entity
            .getDasChildren(ObjectKind.COLUMN)
            .toList()
            .parallelStream()
            .map { Column(entity = it as DasColumn, table = this) }

    fun asLookupElement(
        triggerCompletionOnInsert: Boolean = false,
        withNamespacePrefix: Boolean = false,
    ): LookupElement =
        LookupElementBuilder
            .create(this, this.nameWithoutPrefix)
            .withLookupString("${namespace.name}.${this.nameWithoutPrefix}")
            .withTypeText(namespace.name, true)
            .withIcon(DasPsiWrappingSymbol(entity, project).getIcon(false))
            .withInsertHandler(
                project,
                triggerCompletion = triggerCompletionOnInsert,
                prefix = if (withNamespacePrefix) { namespace.name ?: "" } else { "" }
            )
}
