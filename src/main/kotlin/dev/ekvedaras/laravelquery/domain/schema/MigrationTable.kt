package dev.ekvedaras.laravelquery.domain.schema

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.v4.utils.LookupUtils.Companion.withInsertHandler
import icons.DatabaseIcons

data class MigrationTable(val name: String, val project: Project) {
    fun asExistingTable(): Table? = Table.findFirst(name, project)

    fun asLookupElement() = LookupElementBuilder
        .create(name)
        .withIcon(DatabaseIcons.Table)
        .withInsertHandler(project, true)
}
