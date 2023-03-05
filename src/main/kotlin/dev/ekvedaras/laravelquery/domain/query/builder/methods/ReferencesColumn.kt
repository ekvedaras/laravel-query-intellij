package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.database.psi.DbColumn
import com.intellij.openapi.util.TextRange
import dev.ekvedaras.laravelquery.domain.StringParameter

sealed interface ReferencesColumn {
    fun findColumnReferencedIn(parameter: StringParameter): DbColumn?
}
