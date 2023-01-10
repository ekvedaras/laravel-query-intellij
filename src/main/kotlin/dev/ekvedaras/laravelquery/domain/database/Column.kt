package dev.ekvedaras.laravelquery.domain.database

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.psi.DbColumn
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import dev.ekvedaras.laravelquery.v4.utils.LookupUtils.Companion.withInsertHandler

data class Column(val entity: DasColumn, val table: Table) {
    val project = table.project
    val name = entity.name

    fun asLookupElement(
        withNamespacePrefix: Boolean = false,
        withTablePrefix: Boolean = false,
        alias: String? = null
    ): LookupElement {
        val prefix = buildString {
            append(if (withNamespacePrefix) {
                table.namespace.name
            } else {
                ""
            })
            append(".")
            append(if (withTablePrefix) {
                table.nameWithoutPrefix
            } else {
                ""
            })
        }

        return LookupElementBuilder
            .create(entity, name)
            .withIcon(DasPsiWrappingSymbol(entity, project).getIcon(false))
            .withTailText(
                "  ${entity.dataType}${if (entity.default != null) " = ${entity.default}" else ""}",
                true
            )
            .withTypeText("${entity.comment ?: ""} ${table.nameWithoutPrefix}", true)
            .withLookupString("${alias ?: "${table.namespace.name}.${table.nameWithoutPrefix}"}.${name}")
            .withLookupString("${table.nameWithoutPrefix}.${name}")
            .withInsertHandler(
                project,
                false,
                alias ?: prefix.trim('.')
            )
    }

    fun asDbColumn(): DbColumn = this.table.namespace.dataSource.entity.findElement(this.entity) as DbColumn
}
