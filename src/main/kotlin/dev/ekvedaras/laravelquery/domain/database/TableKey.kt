package dev.ekvedaras.laravelquery.domain.database

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasTableKey
import com.intellij.database.psi.DbTableKey
import com.intellij.sql.symbols.DasPsiWrappingSymbol
import dev.ekvedaras.laravelquery.v4.utils.LookupUtils.Companion.withInsertHandler

data class TableKey(val entity: DasTableKey, val table: Table) {
    val project = table.project
    val name = entity.name
    val columns
        get() = entity.columnsRef
            .resolveObjects()
            .filterIsInstance<DasColumn>()
            .map { Column(it, table) }

    fun asLookupElement(): LookupElement =
        LookupElementBuilder
            .create(entity, name)
            .withIcon(DasPsiWrappingSymbol(entity, project).getIcon(false))
            .withTypeText(
                "  ${if (entity.isPrimary) " primary" else ""}",
                true
            )
            .withTailText("${entity.comment ?: ""} ${entity.columnsRef.names().joinToString(", ")}", true)
            .withInsertHandler(
                project,
                false,
            )

    fun asDbTableKey(): DbTableKey = table.namespace.dataSource.entity.findElement(entity) as DbTableKey
}
