package dev.ekvedaras.laravelquery.domain.database

import com.intellij.database.psi.DbDataSource
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import java.util.stream.Stream

data class DataSource(val entity: DbDataSource) {
    val project = entity.project
    val name = entity.name

    companion object {
        fun list(project: Project): Stream<out DataSource> =
            DbUtil.getDataSources(project)
                .toList()
                .parallelStream()
                .filter { LaravelQuerySettings.getInstance(project).interestedIn(it) }
                .map { DataSource(entity = it) }
    }

    fun namespaces(): Stream<out Namespace> =
        DasUtil.getSchemas(entity)
            .toList()
            .parallelStream()
            .filter { LaravelQuerySettings.getInstance(project).interestedIn(it, entity) }
            .filter { Namespace.isImportant(it) }
            .map { Namespace(entity = it, dataSource = this) }

    fun findNamespace(namespace: String): Namespace? = namespaces().firstWhereOrNull { it.entity.name == namespace }

    fun findFirstTable(table: String): Table? =
        namespaces()
            .firstWhereOrNull { it.findTable(table) != null }
            ?.findTable(table)
}
