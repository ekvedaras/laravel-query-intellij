package dev.ekvedaras.laravelquery.domain.schema

import com.jetbrains.php.lang.psi.elements.PhpClass
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.isChildOfAny

data class Migration(private val clazz: PhpClass) {
    init {
        if (! clazz.isChildOfAny(LaravelClasses.Migration)) throw Exception("Class ${clazz.fqn} in file ${clazz.containingFile.name} does not extend ${LaravelClasses.Migration}")
    }

    private val upMethod = clazz.methods.first { it.name == "up" }
    private val downMethod = clazz.methods.firstOrNull { it.name == "down" }

    private val newTables = setOf<NewTable>()


}
