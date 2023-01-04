package dev.ekvedaras.laravelquery.v4.utils

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasForeignKey
import com.intellij.database.model.DasIndex
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.model.DasTableKey
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbDataSource
import com.intellij.database.psi.DbTable
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.tables
import java.util.stream.Stream
import kotlin.jvm.optionals.getOrNull

private val SchemasToSkip = listOf(
    "sys", "information_schema", "mysql", "performance_schema",
)

class DatabaseUtils private constructor() {
    companion object {
        fun Project.dbDataSourcesInParallel(): Stream<out DbDataSource> =
            DbUtil.getDataSources(this).toList().parallelStream().filter {
                LaravelQuerySettings.getInstance(this).interestedIn(it)
            }

        fun DbDataSource.schemasInParallel(): Stream<out DasNamespace> =
            DasUtil.getSchemas(this).toList().parallelStream().filter {
                LaravelQuerySettings.getInstance(this.project).interestedIn(it, this)
            }.filter { !SchemasToSkip.contains(it.name) }

        fun DbDataSource.tables() =
            DasUtil.getTables(this).filter {
                LaravelQuerySettings.getInstance(this.project).interestedIn(it, this)
            }.filter { !it.isSystem && !SchemasToSkip.contains(it.dasParent?.name) }.filter {
                it.isPrefixed(this.project)
            }

        fun DbDataSource.tablesInParallel(): Stream<out DasTable> =
            DasUtil.getTables(this).toList().parallelStream().filter {
                LaravelQuerySettings.getInstance(this.project).interestedIn(it, this)
            }.filter {
                !it.isSystem && !SchemasToSkip.contains(it.dasParent?.name)
            }.filter { it.isPrefixed(this.project) }

        fun DasNamespace.tablesInParallel(project: Project): Stream<out DasTable> =
            this.getDasChildren(ObjectKind.TABLE).toList().parallelStream()
                .map { it as DasTable }
                .filter { !it.isSystem }
                .filter { it.isPrefixed(project) }

        fun DasTable.columnsInParallel(): Stream<out DasColumn> =
            this.getDasChildren(ObjectKind.COLUMN).toList().parallelStream().map { it as DasColumn }

        fun DasTable.indexesInParallel(): Stream<out DasIndex> =
            this.getDasChildren(ObjectKind.INDEX).toList().parallelStream().map { it as DasIndex }

        fun DasTable.keysInParallel(): Stream<out DasTableKey> =
            this.getDasChildren(ObjectKind.KEY).toList().parallelStream().map { it as DasTableKey }

        fun DasTable.foreignKeysInParallel(): Stream<out DasForeignKey> =
            this.getDasChildren(ObjectKind.FOREIGN_KEY).toList().parallelStream().map { it as DasForeignKey }

        private fun DasTable.isPrefixed(project: Project): Boolean =
            this.name.startsWith(LaravelQuerySettings.getInstance(project).tablePrefix)

        fun DasTable.nameWithoutPrefix(project: Project): String =
            this.name.substringAfter(LaravelQuerySettings.getInstance(project).tablePrefix)

        fun DasColumn.tableNameWithoutPrefix(project: Project): String =
            this.tableName.substringAfter(LaravelQuerySettings.getInstance(project).tablePrefix)

        fun String.withoutTablePrefix(project: Project): String =
            this.substringAfter(LaravelQuerySettings.getInstance(project).tablePrefix)
    }
}
