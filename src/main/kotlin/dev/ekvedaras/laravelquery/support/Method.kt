package dev.ekvedaras.laravelquery.support

import com.intellij.openapi.project.DumbService
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.AssignmentExpression
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.ParenthesizedExpression
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.Variable

fun MethodReference.classReference(depth: Int = 0): ClassReference? =
    if (firstPsiChild is ClassReference) firstPsiChild as ClassReference
    else if (firstPsiChild is ParenthesizedExpression && firstPsiChild?.firstPsiChild is NewExpression && firstPsiChild?.firstPsiChild?.firstPsiChild is ClassReference) firstPsiChild!!.firstPsiChild!!.firstPsiChild as ClassReference
    else if (classReference is ClassReference) classReference as ClassReference
    else if (depth < 30 && classReference is MethodReference && classReference != this) (classReference as MethodReference).classReference(depth + 1)
    else if (depth < 30 && classReference is MethodReference && classReference == this && firstPsiChild is MethodReference) (firstPsiChild as MethodReference).classReference(depth + 1)
    else null

fun MethodReference.isMemberOfAny(vararg classes: String): Boolean =
    if (DumbService.isDumb(project)) false
    else if (classReference !is PhpTypedElement) false
    else PhpIndex.getInstance(project)
        .completeType(project, (classReference as PhpTypedElement).type, mutableSetOf())
        .types
        .flatMap { PhpIndex.getInstance(project).getClassesByFQN(it) }
        .flatMap { it.superClasses.map { parent -> parent.fqn } + it.fqn }
        .containsAny(*classes)

fun MethodReference.isCalledOnAVariable() = firstPsiChild is Variable
fun MethodReference.isAssignedToAVariable() = firstPsiChild is AssignmentExpression
fun MethodReference.referenceVariable() = when {
    isCalledOnAVariable() -> firstPsiChild as? Variable
    isAssignedToAVariable() -> firstPsiChild?.firstPsiChild as? Variable
    else -> null
}
