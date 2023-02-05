package dev.ekvedaras.laravelquery.domain.model

import com.cesarferreira.pluralize.pluralize
import com.intellij.openapi.project.DumbService
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.database.DataSource
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.query.queryVariable.QueryVariable
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.cleanup
import dev.ekvedaras.laravelquery.support.firstWhereOrNull
import dev.ekvedaras.laravelquery.support.isChildOfAny
import dev.ekvedaras.laravelquery.support.tryTransforming

/**
 * This is a wrapper class for PHP classes that are Eloquent models.
 *
 * All the logic for fetching relations, table names and whatever else is directly related to model, should live here.
 */
data class Model(private val clazz: PhpClass) {
    init {
        if (!clazz.isChildOfAny(LaravelClasses.Model)) {
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

    fun relation(name: String): Model? =
        clazz
            .methods
            .firstOrNull { it.name == name }
            ?.tryTransforming { Relation(it) }
            ?.model

    companion object {
        fun from(classReference: ClassReference): Model? {
            if (DumbService.isDumb(classReference.project)) return null

            return PhpIndex.getInstance(classReference.project)
                .getClassesByFQN(classReference.fqn ?: return null)
                .firstOrNull()
                .tryTransforming { Model(it) }
        }

        fun from(fqn: StringLiteralExpression): Model? {
            if (DumbService.isDumb(fqn.project)) return null

            return PhpIndex.getInstance(fqn.project)
                .getClassesByFQN(fqn.text.cleanup())
                .firstOrNull()
                .tryTransforming { Model(it) }
        }

        private fun PsiElement.isWithinModel(): Boolean = parentOfType<PhpClass>()?.isChildOfAny(LaravelClasses.Model) == true
        fun PsiElement.isWithinModelScopeMethod(): Boolean = isWithinModel() && parentOfType<Function>()?.name?.startsWith("scope") == true
        fun QueryVariable.isInsideModelScope(): Boolean = variable.isWithinModelScopeMethod() && variable.parentOfType<Function>()?.getParameter(0)?.name == variable.name
    }

    override fun equals(other: Any?): Boolean
    {
        if (other !is Model) return false

        return clazz == other.clazz
    }

    override fun hashCode(): Int {
        return clazz.hashCode()
    }
}
