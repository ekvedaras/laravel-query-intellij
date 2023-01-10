package dev.ekvedaras.laravelquery.domain.model

import com.cesarferreira.pluralize.pluralize
import com.intellij.openapi.project.DumbService
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import dev.ekvedaras.laravelquery.domain.database.DataSource
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.cleanup
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import dev.ekvedaras.laravelquery.support.isChildOf
import dev.ekvedaras.laravelquery.support.transform

class Model private constructor(private val clazz: PhpClass) {
    init {
        if (!clazz.isChildOf(LaravelClasses.Model)) {
            throw Exception("Given class does not extend Eloquent base model thus is not a model")
        }
    }

    private val tableField = clazz.findFieldByName("table", false)
    private val definedTableName =
        if (tableField is Field) tableField.defaultValue?.text?.cleanup()
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
                .getClassesByFQN(classReference.fqn ?: return null)
                .firstOrNull()
                .transform {
                    try {
                        Model(it)
                    } catch (e: Exception) {
                        null
                    }
                }
        }
    }

    override fun equals(other: Any?): Boolean
    {
        if (other !is Model) return false

        return this.clazz == other.clazz
    }

    override fun hashCode(): Int {
        return clazz.hashCode()
    }
}
