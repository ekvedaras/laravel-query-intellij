package dev.ekvedaras.laravelquery.domain.Model

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass

class Model(val clazz: PhpClass) {
    private val tableField = clazz.findFieldByName("table", false)

    val table = if (tableField is Field) {
        tableField.defaultValue
    } else {
        null // TODO resolve from model name
    }
}
