package dev.ekvedaras.laravelquery.domain.Query

import com.intellij.database.psi.DbDataSource
import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable

class Query {
    private var statements: List<QueryStatement> = listOf()

    val datasource: DbDataSource? = null
    val databases: Set<DbNamespace> = setOf()
    val tables: Set<DbTable> = setOf()
}
