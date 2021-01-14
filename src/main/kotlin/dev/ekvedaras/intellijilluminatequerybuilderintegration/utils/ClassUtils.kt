package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.jetbrains.php.lang.psi.elements.PhpClass

class ClassUtils {
    companion object {
        @JvmStatic
        fun PhpClass.isChildOf(clazz: PhpClass): Boolean
        {
            if (this == clazz) {
                return true
            }

            if (superClass == null) {
                return false
            }

            return superClass!!.isChildOf(clazz)
        }
    }
}