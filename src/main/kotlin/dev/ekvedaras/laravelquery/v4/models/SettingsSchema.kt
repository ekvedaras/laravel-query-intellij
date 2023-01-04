package dev.ekvedaras.laravelquery.v4.models

import com.intellij.database.model.DasNamespace
import com.intellij.database.psi.DbDataSource

class SettingsSchema(
    val dataSource: DbDataSource,
    val schema: DasNamespace
) {
    companion object {
        private const val separator = "<-/lq/->"
        fun keyFor(schemaName: String, dataSourceId: String) = dataSourceId + separator + schemaName

        fun keyFor(dasNamespace: DasNamespace, dbDataSource: DbDataSource) =
            keyFor(dasNamespace.name, dbDataSource.uniqueId)
    }

    override fun toString(): String = schema.name
    fun key(): String = keyFor(schema, dataSource)
}
