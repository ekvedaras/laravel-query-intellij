package dev.ekvedaras.laravelquery.domain

import com.intellij.database.psi.DbTableKey

interface ReferencesTableKey {
    fun findTableKeyReferencedIn(parameter: StringParameter): DbTableKey?
}
