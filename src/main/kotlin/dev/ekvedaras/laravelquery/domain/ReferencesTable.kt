package dev.ekvedaras.laravelquery.domain

import com.intellij.database.psi.DbTable

interface ReferencesTable {
    fun findTableReferencedIn(parameter: StringParameter): DbTable?
}
