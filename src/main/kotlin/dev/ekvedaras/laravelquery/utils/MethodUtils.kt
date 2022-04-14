package dev.ekvedaras.laravelquery.utils

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.database.util.isInstanceOf
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.util.ArrayUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpPsiElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.ArrayCreationExpressionImpl
import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpClassAliasImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl
import dev.ekvedaras.laravelquery.utils.ClassUtils.Companion.isChildOf
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isJoinOrRelation

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

        fun resolveMethodTypeClasses(method: MethodReference, project: Project): List<PhpClassImpl> {
            if (
                DumbService.isDumb(project) ||
                !ApplicationManager.getApplication().isReadAccessAllowed
            ) {
                return listOf()
            }

            val classes = mutableListOf<PhpClassImpl>()

            PhpIndex
                .getInstance(project)
                .completeType(project, method.declaredType, null)
                .types
                .toList()
                .forEach { collectClasses(project, it, classes) }

            return classes
        }

        fun resolveMethodClasses(method: MethodReference, project: Project): List<PhpClassImpl> {
            if (
                DumbService.isDumb(project) ||
                method.classReference == null ||
                !ApplicationManager.getApplication().isReadAccessAllowed
            ) {
                return listOf()
            }

            val classes = mutableListOf<PhpClassImpl>()

            PhpIndex
                .getInstance(project)
                .completeType(project, method.classReference?.declaredType ?: return listOf(), null)
                .types
                .toList()
                .forEach { collectClasses(project, it, classes) }

            return classes
        }

        private fun collectClasses(
            project: Project,
            className: String?,
            classes: MutableList<PhpClassImpl>
        ) {
            PhpIndex.getInstance(project)
                .getClassesByFQN(className)
                .forEach classLoop@{ clazz ->
                    when (clazz) {
                        is PhpClassAliasImpl -> {
                            if (clazz.original != null) {
                                classes.add(clazz.original as PhpClassImpl)
                            }
                        }
                        is PhpClassImpl -> classes.add(clazz)
                    }
                }
        }

        fun findMethodsInTree(root: PsiElement?): MutableList<MethodReference> {
            val list = mutableListOf<MethodReference>()
            if (root == null) {
                return list
            }

            if (root.textMatches("return") || root.textMatches(" ")) {
                return findMethodsInTree(root.nextSibling)
            }

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

        fun MethodReference.isSameInSameFile(that: MethodReference) = this.absoluteRange == that.absoluteRange

        private fun findMethodsInTree(root: PsiElement?, list: MutableList<MethodReference>) {
            if (root == null) {
                return
            }

            for (child in root.children) {
                if (child is MethodReference) {
                    list.add(child)
                    findMethodsInTree(child, list)
                }
            }
        }
    }
}

fun CompletionParameters.findParamIndex(): Int =
    this.position.findParamIndex()

fun PsiElement.findParamIndex(allowArray: Boolean = false): Int {
    val parent = this.parent ?: return -1

    return if (parent is ParameterList) {
        ArrayUtil.indexOf(parent.parameters, this)
    } else if (allowArray && parent is ArrayCreationExpressionImpl) {
        parent.children.indexOfFirst { it === this }
    } else {
        this.parent?.findParamIndex(allowArray) ?: -1
    }
}

fun PsiElement.findParameterListUp(): ParameterList? =
    if (this is ParameterList) this
    else this.parent?.findParameterListUp()

fun PsiElement.findParameterListDown(): ParameterList? =
    if (this is ParameterList) this
    else this.children.firstOrNull { it.findParameterListDown() != null }?.findParameterListDown()

fun MethodReference.isJoinOrRelation(project: Project): Boolean =
    MethodUtils.resolveMethodClasses(this, project).any { it.isJoinOrRelation() }

fun MethodReference.isInsideModelQueryClosure(project: Project): Boolean =
    (this.getParentOfClosure()?.classReference as? MethodReference)
        ?.classReference
        ?.getClass(project)
        ?.isChildOf(LaravelClasses.Model) == true

fun MethodReference.firstChildOfParentStatement(): PhpPsiElement? =
    this.parentOfType<Statement>()?.firstPsiChild

fun MethodReference.getParentOfClosure(): MethodReference? =
    this.parentOfType<FunctionImpl>()?.parentOfType()

fun PhpTypedElement.getClass(project: Project): PhpClassImpl? =
    PhpIndex.getInstance(project)
        .getClassesByFQN(this.declaredType.types.firstOrNull() ?: "")
        .firstOrNull() as? PhpClassImpl
