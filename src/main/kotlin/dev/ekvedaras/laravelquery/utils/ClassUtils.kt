package dev.ekvedaras.laravelquery.utils

import com.cesarferreira.pluralize.pluralize
import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.impl.PhpClassAliasImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl

class ClassUtils private constructor() {
    companion object {
        @JvmStatic
        fun PhpClassImpl.isChildOf(clazz: PhpClass): Boolean {
            if (this.fqn == clazz.fqn) {
                return true
            }

            if (superClass == null) {
                return false
            }

            if (superClass is PhpClassAliasImpl) {
                val original = (superClass as PhpClassAliasImpl).original ?: return false
                return (original as PhpClassImpl).isChildOf(clazz)
            }

            return superClass != null && (superClass as PhpClassImpl).isChildOf(clazz)
        }

        @JvmStatic
        fun PhpClassImpl.isChildOf(clazz: String, depth: Int = 1): Boolean {
            if (this.fqn == clazz.asPhpClass(project)?.fqn) {
                return true
            }

            if (superClass == null) {
                return false
            }

            if (superClass is PhpClassAliasImpl) {
                val original = (superClass as PhpClassAliasImpl).original ?: return false
                return (original as PhpClassImpl).isChildOf(clazz)
            }

            if (depth > 20) {
                return false
            }

            return (superClass as PhpClassImpl).isChildOf(clazz, depth + 1)
        }

        @JvmStatic
        fun String.asPhpClass(project: Project): PhpClass? {
            return PhpIndex.getInstance(project).getClassesByFQN(this).firstOrNull()
        }

        @JvmStatic
        fun PhpClass.asTableName(): String {
            val table = this.name.fold(StringBuilder(this.name.length)) { acc, c ->
                if (c in 'A'..'Z') (if (acc.isNotEmpty()) acc.append('_') else acc).append(c + ('a' - 'A'))
                else acc.append(c)
            }.toString()

            val parts = table.split("_")

            if (parts.size == 1) {
                return table.pluralize()
            }

            val last = parts[parts.size - 1]
            return parts.subList(0, parts.size - 1).joinToString("_") + "_" + last.pluralize()
        }

        fun fieldHasDefaultValue(field: Field?) = field != null && field.defaultValue != null
    }
}
