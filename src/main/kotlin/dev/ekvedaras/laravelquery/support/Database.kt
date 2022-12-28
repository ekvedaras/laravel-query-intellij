package dev.ekvedaras.laravelquery.support

import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbDataSource
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import java.util.stream.Stream

private val unimportantSchemas = listOf("sys", "information_schema", "mysql", "performance_schema")

class Database private constructor() {
    companion object {
        fun dataSourcesInParallel(project: Project): Stream<out DbDataSource> =
            DbUtil.getDataSources(project)
                .toList()
                .parallelStream()
                .filter { LaravelQuerySettings.getInstance(project).interestedIn(it) }

        fun findFirstTable(project: Project, name: String, namespace: String?): DasTable? {
            val tables = mutableListOf<DasTable>()

            dataSourcesInParallel(project).forEach { dataSource ->
                if (tables.size > 0) return@forEach

                if (namespace != null) {
                    (dataSource.findNamespace(namespace)?.findTable(name, project) ?: return@forEach).also { tables += it }
                }

                (dataSource.findFirstTable(name) ?: return@forEach).also { tables += it }
            }

            return tables.firstOrNull()
        }
    }
}

private fun DasTable.isPrefixed(project: Project): Boolean =
    this.name.startsWith(LaravelQuerySettings.getInstance(project).tablePrefix)

fun DbDataSource.namespacesInParallel(): Stream<out DasNamespace> =
    DasUtil.getSchemas(this)
        .toList()
        .parallelStream()
        .filter { LaravelQuerySettings.getInstance(this.project).interestedIn(it, this) }
        .filter { !unimportantSchemas.contains(it.name) }

fun DbDataSource.tablesInParallel(): Stream<out DasTable> =
    DasUtil.getTables(this)
        .toList()
        .parallelStream()
        .filter { LaravelQuerySettings.getInstance(this.project).interestedIn(it, this) }
        .filter { !it.isSystem && !unimportantSchemas.contains(it.dasParent?.name) }
        .filter { it.isPrefixed(this.project) }

fun DasNamespace.tablesInParallel(project: Project): Stream<out DasTable> =
    this.getDasChildren(ObjectKind.TABLE)
        .toList()
        .parallelStream()
        .map { it as DasTable }
        .filter { !it.isSystem }
        .filter { it.isPrefixed(project) }

fun DbDataSource.findNamespace(name: String): DasNamespace? = namespacesInParallel().firstWhereOrNull { it.name == name }

fun DasNamespace.findTable(name: String, project: Project): DasTable? = tablesInParallel(project).firstWhereOrNull { it.name == name }

fun DbDataSource.findFirstTable(name: String): DasTable? = tablesInParallel().firstWhereOrNull { it.name == name }

fun DasTable.getNamespace(): DasNamespace = this.dasParent as DasNamespace
