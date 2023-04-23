package dev.ekvedaras.laravelquery.domain

import com.intellij.database.psi.DbIndex

interface ReferencesIndex {
    fun findIndexReferencedIn(parameter: StringParameter): DbIndex?
}
