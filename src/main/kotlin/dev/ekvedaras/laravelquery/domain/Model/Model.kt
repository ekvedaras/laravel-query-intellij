package dev.ekvedaras.laravelquery.domain.Model

import com.cesarferreira.pluralize.pluralize
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup

class Model(val clazz: PhpClass) {
    private val tableField = clazz.findFieldByName("table", false)

    val table: String? = if (tableField is Field) {
        tableField.defaultValue?.text?.unquoteAndCleanup()
    } else {
        val table = clazz.name.fold(StringBuilder(clazz.name.length)) { acc, c ->
            if (c in 'A'..'Z') (if (acc.isNotEmpty()) acc.append('_') else acc).append(c + ('a' - 'A'))
            else acc.append(c)
        }.toString()

        val parts = table.split('_')

        if (parts.size == 1) {
            table.pluralize()
        } else {
            val last = parts[parts.size - 1]
            "${parts.subList(0, parts.size - 1).joinToString("_")}_${last.pluralize()}"
        }
    }
}
