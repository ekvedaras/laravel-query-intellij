package dev.ekvedaras.laravelquery.domain

import com.intellij.database.psi.DbColumn

interface ReferencesColumn {
    fun findColumnReferencedIn(parameter: StringParameter): DbColumn?
}
