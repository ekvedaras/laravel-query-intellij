package dev.ekvedaras.laravelquery.domain.schema

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.schema.builder.methods.MigratesNamespace
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.v4.utils.LookupUtils.Companion.withInsertHandler
import icons.DatabaseIcons

data class MigrationNamespace(val methodCall : MigratesNamespace) {
    val name = methodCall.namespaceParameter?.namespaceName
    val project = methodCall.reference.project

    fun asExistingNamespace(): Namespace? = name.transform { Namespace.findFirst(it, project) }

    fun asLookupElement() = LookupElementBuilder
        .create(name ?: methodCall.reference)
        .withIcon(DatabaseIcons.Schema)
        .withInsertHandler(project, triggerCompletion = false)
}
