package dev.ekvedaras.laravelquery.domain.database

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbTable
import com.intellij.openapi.project.Project
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import dev.ekvedaras.laravelquery.v4.utils.LookupUtils.Companion.withInsertHandler
import java.util.stream.Stream

data class Table(val entity: DasTable, val namespace: Namespace) {
    val project = namespace.project
    val name = entity.name
    val nameWithoutPrefix = name.substringAfter(LaravelQuerySettings.getInstance(project).tablePrefix)

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
        entity
            .getDasChildren(ObjectKind.COLUMN)
            .toList()
            .parallelStream()
            .map { Column(entity = it as DasColumn, table = this) }

    fun asLookupElement(
        triggerCompletionOnInsert: Boolean = false,
        withNamespacePrefix: Boolean = false,
    ): LookupElement =
        LookupElementBuilder
            .create(this, nameWithoutPrefix)
            .withLookupString("${namespace.name}.${nameWithoutPrefix}")
            .withTypeText(namespace.name, true)
            .withIcon(DasPsiWrappingSymbol(entity, project).getIcon(false))
            .withInsertHandler(
                project,
                triggerCompletion = triggerCompletionOnInsert,
                prefix = if (withNamespacePrefix) {
                    namespace.name
                } else {
                    ""
                }
            )

    fun asDbTable(): DbTable = namespace.dataSource.entity.findElement(entity) as DbTable

    fun findColumn(name: String) = columns().firstWhereOrNull { it.name == name }
}
