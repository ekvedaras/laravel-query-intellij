package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.ArrayUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isJoinOrRelation

class MethodUtils private constructor() {
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

        fun resolveMethodClasses(method: MethodReference, project: Project): List<PhpClassImpl> {
            if (DumbService.isDumb(project) || method.classReference == null) {
                return listOf()
            }

            val classes = mutableListOf<PhpClassImpl>()

            PhpIndex
                .getInstance(project)
                .completeType(project, method.classReference!!.declaredType, null)
                .types
                .toList()
                .forEach {
                    PhpIndex.getInstance(project)
                        .getClassesByFQN(it)
                        .forEach { clazz -> classes.add(clazz as PhpClassImpl) }
                }

            return classes
        }

        fun CompletionParameters.findParamIndex(): Int =
            this.position.findParamIndex()

        fun PsiElement.findParamIndex(): Int {
            val parent = this.parent ?: return -1

            return if (parent is ParameterList) {
                ArrayUtil.indexOf(parent.parameters, this)
            } else this.parent.findParamIndex()
        }

        fun PsiElement.findParameterList(): ParameterList? =
            if (this is ParameterList) this
            else this.parent.findParameterList()

        fun findMethodsInTree(root: PsiElement): MutableList<MethodReference> {
            if (root.textMatches("return") || root.textMatches(" ")) {
                return findMethodsInTree(root.nextSibling)
            }

            val list = mutableListOf<MethodReference>()

            if (root is MethodReference) {
                list.add(root)
            }

            findMethodsInTree(root, list)
            return list
        }

        fun firstChildOfType(parent: PsiElement, type: String): PsiElement? {
            for (child in parent.children) {
                if (child.javaClass.name == type) {
                    return child
                } else if (child.children.isNotEmpty()) {
                    val find = firstChildOfType(child, type)
                    if (find != null) {
                        return find
                    }
                }
            }

            return null
        }

        fun MethodReference.isJoinOrRelation(project: Project): Boolean =
            resolveMethodClasses(this, project).any { it.isJoinOrRelation() }

        fun PhpTypedElement.getClass(project: Project): PhpClassImpl =
            PhpIndex.getInstance(project).getClassesByFQN(this.declaredType.types.first()).first() as PhpClassImpl

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
