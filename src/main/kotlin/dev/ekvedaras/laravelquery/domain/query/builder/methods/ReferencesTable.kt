package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.database.psi.DbTable
import dev.ekvedaras.laravelquery.domain.StringParameter

sealed interface ReferencesTable : ReferencesNamespace {
    fun findTableReferencedIn(parameter: StringParameter): DbTable?
}
