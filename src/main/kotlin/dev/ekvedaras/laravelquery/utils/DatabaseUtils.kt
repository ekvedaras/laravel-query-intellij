package dev.ekvedaras.laravelquery.utils

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasIndex
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.model.DasTableKey
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbDataSource
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.openapi.project.Project
import java.util.stream.Stream

private val SchemasToSkip = listOf(
    "sys", "information_schema", "mysql", "performance_schema",
)

class DatabaseUtils private constructor() {
    companion object {
        fun Project.dbDataSourcesInParallel(): Stream<out DbDataSource> =
            DbUtil.getDataSources(this).toList().parallelStream()

        fun DbDataSource.schemasInParallel(): Stream<out DasNamespace> =
            DasUtil.getSchemas(this).toList().parallelStream().filter { !SchemasToSkip.contains(it.name) }

        fun DbDataSource.tables() =
            DasUtil.getTables(this).filter { !it.isSystem && !SchemasToSkip.contains(it.dasParent?.name) }

        fun DbDataSource.tablesInParallel(): Stream<out DasTable> =
            DasUtil.getTables(this).toList().parallelStream().filter {
                !it.isSystem && !SchemasToSkip.contains(it.dasParent?.name)
            }

        fun DasNamespace.tablesInParallel(): Stream<out DasTable> =
            this.getDasChildren(ObjectKind.TABLE).toList().parallelStream()
                .map { it as DasTable }
                .filter { !it.isSystem }

        fun DasTable.columnsInParallel(): Stream<out DasColumn> =
            this.getDasChildren(ObjectKind.COLUMN).toList().parallelStream().map { it as DasColumn }

        fun DasTable.indexesInParallel(): Stream<out DasIndex> =
            this.getDasChildren(ObjectKind.INDEX).toList().parallelStream().map { it as DasIndex }

        fun DasTable.keysInParallel(): Stream<out DasTableKey> =
            this.getDasChildren(ObjectKind.KEY).toList().parallelStream().map { it as DasTableKey }
    }
}
