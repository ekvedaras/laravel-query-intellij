package dev.ekvedaras.laravelquery.models

import com.intellij.database.model.DasNamespace
import com.intellij.database.psi.DbDataSource

class SettingsSchema(
    val dataSource: DbDataSource,
    val schema: DasNamespace
) {
    companion object {
        const val separator = "<-/lq/->"
    }
    override fun toString(): String = schema.name
    fun key(): String = "${dataSource.uniqueId}$separator${schema.name}"
}
