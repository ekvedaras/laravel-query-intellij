package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import dev.ekvedaras.laravelquery.domain.StringParameter

sealed interface ReferencesNamespace {
    fun findNamespaceReferencedIn(parameter: StringParameter): DbNamespace?
}
