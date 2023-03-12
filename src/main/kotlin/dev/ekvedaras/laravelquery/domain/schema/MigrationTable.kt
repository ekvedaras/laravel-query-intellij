package dev.ekvedaras.laravelquery.domain.schema

import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.domain.database.Table

data class MigrationTable(val name: String, val project: Project) {
    fun asExistingTable(): Table? = Table.findFirst(name, project)
}
