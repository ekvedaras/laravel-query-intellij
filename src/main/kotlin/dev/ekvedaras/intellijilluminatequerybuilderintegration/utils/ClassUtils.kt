package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference

class ClassUtils {
    companion object {
        fun resolveMethodReference(element: PsiElement?, depthLimit: Int = 10): MethodReference? {
            if (element == null || depthLimit <= 0) {
                return null
            }

            if (element.parent is MethodReference) {
                return element.parent as MethodReference
            }

            return resolveMethodReference(element.parent, depthLimit - 1)
        }

        fun resolveMethodClasses(method: MethodReference): List<String> {
            return PhpIndex
                .getInstance(method.project)
                .completeType(method.project, method.inferredType, null)
                .types
                .toList()
        }

        fun findMethodsInTree(root: PsiElement): List<MethodReference> {
            val list = mutableListOf<MethodReference>()
            findMethodsInTree(root, list)
            return list
        }

        private fun findMethodsInTree(root: PsiElement, list: MutableList<MethodReference>) {
            for (child in root.children) {
                if (child is MethodReference) {
                    list.add(child)
                    findMethodsInTree(child, list)
                }
            }
        }
    }
}