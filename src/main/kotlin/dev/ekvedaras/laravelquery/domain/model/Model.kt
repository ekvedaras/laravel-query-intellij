package dev.ekvedaras.laravelquery.domain.model

import com.cesarferreira.pluralize.pluralize
import com.intellij.openapi.project.DumbService
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import dev.ekvedaras.laravelquery.domain.database.DataSource
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import dev.ekvedaras.laravelquery.support.transform
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup

class Model(private val clazz: PhpClass) {
    private val tableField = clazz.findFieldByName("table", false)
    private val definedTableName =
        if (tableField is Field) tableField.defaultValue?.text?.unquoteAndCleanup()
        else null
    private val resolvedTableName: String
        get() {
            val table = clazz.name.fold(StringBuilder(clazz.name.length)) { acc, c ->
                if (c in 'A'..'Z') (if (acc.isNotEmpty()) acc.append('_') else acc).append(c + ('a' - 'A'))
                else acc.append(c)
            }.toString()

            val parts = table.split('_')

            return if (parts.size == 1) table.pluralize()
            else {
                val last = parts[parts.size - 1]
                "${parts.subList(0, parts.size - 1).joinToString("_")}_${last.pluralize()}"
            }
        }

    private val tableName = definedTableName ?: resolvedTableName

    val table: Table? = DataSource.list(clazz.project)
        .firstWhereOrNull { it.findFirstTable(tableName) != null }
        ?.findFirstTable(tableName)

    companion object {
        fun from(classReference: ClassReference): Model? {
            if (DumbService.isDumb(classReference.project)) return null

            return PhpIndex.getInstance(classReference.project)
                .getClassesByFQN(classReference.type.types.firstOrNull() ?: return null)
                .firstOrNull()
                .transform { Model(it) }
        }
    }
}
