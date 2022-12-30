package dev.ekvedaras.laravelquery.domain.database

import com.intellij.database.model.DasColumn

data class Column(val entity: DasColumn, val table: Table) {
    val project = table.project
    val name = entity.name
}
