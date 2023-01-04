package dev.ekvedaras.laravelquery.domain.database

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.openapi.project.Project
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import dev.ekvedaras.laravelquery.v4.utils.LookupUtils.Companion.buildLookup
import dev.ekvedaras.laravelquery.v4.utils.LookupUtils.Companion.withInsertHandler
import java.util.stream.Stream

private val unimportantSchemas = listOf("sys", "information_schema", "mysql", "performance_schema")

data class Namespace(val entity: DasNamespace, val dataSource: DataSource) {
    val project = dataSource.project
    val name = entity.name

    companion object {
        fun list(project: Project): Stream<out Namespace> = DataSource.list(project).flatMap { it.namespaces() }
        fun isImportant(namespace: DasNamespace) = ! unimportantSchemas.contains(namespace.name)
        fun findFirst(namespace: String, project: Project) =
            DataSource.list(project)
                .firstWhereOrNull{ it.findNamespace(namespace) != null }
                ?.findNamespace(namespace)
    }

    fun tables(): Stream<out Table> =
        this.entity
            .getDasChildren(ObjectKind.TABLE)
            .toList()
            .parallelStream()
            .map { it as DasTable }
            .filter { !it.isSystem }
            .filter { Table.isPrefixed(it, dataSource.project) }
            .map { Table(entity = it, namespace = this) }

    fun findTable(table: String): Table? = this.tables().firstWhereOrNull{ it.name == table }

    fun asLookupElement(): LookupElement = LookupElementBuilder
        .create(this, this.name)
        .withIcon(DasPsiWrappingSymbol(entity, project).getIcon(false))
        .withTypeText(dataSource.name, true)
        .withInsertHandler(project, true)
}
