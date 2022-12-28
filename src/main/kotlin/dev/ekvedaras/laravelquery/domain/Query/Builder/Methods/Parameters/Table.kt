package dev.ekvedaras.laravelquery.domain.Query.Builder.Methods.Parameters

import com.intellij.database.model.DasTable
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.findFirstTable
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.findNamespace
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.findTable

class Table(val element: StringLiteralExpression) {
    val name: String
    val database: String?
    val alias: String?

    init {
        val reference: String;

        if (element.text.contains("as", ignoreCase = true)) {
            reference = element.text.lowercase().substringBefore("as").trim()
            this.alias = element.text.lowercase().substringAfter("as").trim()
        } else {
            reference = element.text.lowercase()
            this.alias = null
        }

        val parts = reference.split('.').reversed()

        this.name = parts[0]
        this.database = parts.getOrNull(1)
    }

    fun asDasTable(): DasTable? {
        val tables = mutableListOf<DasTable>()

        element.project.dbDataSourcesInParallel().forEach { dataSource ->
            (
                if (this.database != null) {
                    dataSource.findNamespace(name = database)?.findTable(this.name)
                } else {
                    dataSource.findFirstTable(this.name)
                } ?: return@forEach
            ).also { tables += it }
        }

        return tables.firstOrNull()
    }
}
