package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.cesarferreira.pluralize.pluralize
import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl

class ClassUtils {
    companion object {
        @JvmStatic
        fun PhpClassImpl.isChildOf(clazz: PhpClass): Boolean
        {
            if (this.fqn == clazz.fqn) {
                return true
            }

            if (superClass == null) {
                return false
            }

            return (superClass as PhpClassImpl).isChildOf(clazz)
        }

        @JvmStatic
        fun PhpClassImpl.isChildOf(clazz: String): Boolean
        {
            if (this.fqn == clazz.asPhpClass(project)?.fqn) {
                return true
            }

            if (superClass == null) {
                return false
            }

            return (superClass as PhpClassImpl).isChildOf(clazz)
        }

        @JvmStatic
        fun String.asPhpClass(project: Project): PhpClass?
        {
            return PhpIndex.getInstance(project).getClassesByFQN(this).first()
        }

        @JvmStatic
        fun PhpClass.asTableName(): String
        {
            val table = this.name.fold(StringBuilder(this.name.length)) { acc, c ->
                if (c in 'A'..'Z') (if (acc.isNotEmpty()) acc.append('_') else acc).append(c + ('a' - 'A'))
                else acc.append(c)
            }.toString()

            val parts = table.split("_");

            /**
             * TOOD there is a bug in the library
             * Might need to fork or something if not fixed soon.
             * @see https://github.com/cesarferreira/kotlin-pluralizer/pull/5
             */

            if (parts.size == 1) {
                return table.pluralize()
            }

            val last = parts[parts.size - 1]
            return parts.subList(0, parts.size - 2).joinToString("_") + last.pluralize()
        }
    }
}