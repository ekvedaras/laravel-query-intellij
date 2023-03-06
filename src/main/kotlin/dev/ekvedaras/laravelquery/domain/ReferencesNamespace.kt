package dev.ekvedaras.laravelquery.domain

import com.intellij.database.psi.DbNamespace

interface ReferencesNamespace {
    fun findNamespaceReferencedIn(parameter: StringParameter): DbNamespace?
}
