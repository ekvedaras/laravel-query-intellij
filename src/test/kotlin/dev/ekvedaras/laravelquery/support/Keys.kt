package dev.ekvedaras.laravelquery.support

import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.domain.database.TableKey

internal sealed interface Keys {
    fun table(): Tables
    fun keyName(): String

    fun find(project: Project): TableKey = table()
        .find(project)
        .findTableKey(keyName())
        ?: throw Exception("Cannot find ${table().namespace().name}.${table().name}.${keyName()} table key")
}
