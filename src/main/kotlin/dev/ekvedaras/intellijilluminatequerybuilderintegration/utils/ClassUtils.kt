package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

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
    }
}