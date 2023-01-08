package dev.ekvedaras.laravelquery.support

import com.intellij.openapi.project.DumbService
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.NewExpression
import com.jetbrains.php.lang.psi.elements.ParenthesizedExpression
import com.jetbrains.php.lang.psi.elements.PhpTypedElement

fun MethodReference.classReference(depth: Int = 0): ClassReference? =
    if (this.firstPsiChild is ClassReference) this.firstPsiChild as ClassReference
    else if (this.firstPsiChild is ParenthesizedExpression && this.firstPsiChild?.firstPsiChild is NewExpression && this.firstPsiChild?.firstPsiChild?.firstPsiChild is ClassReference) this.firstPsiChild!!.firstPsiChild!!.firstPsiChild as ClassReference
    else if (this.classReference is ClassReference) this.classReference as ClassReference
    else if (depth < 30 && this.classReference is MethodReference && this.classReference != this) (this.classReference as MethodReference).classReference(depth + 1)
    else if (depth < 30 && this.classReference is MethodReference && this.classReference == this && this.firstPsiChild is MethodReference) (this.firstPsiChild as MethodReference).classReference(depth + 1)
    else null

fun MethodReference.isMemberOfAny(vararg classes: String): Boolean =
    if (DumbService.isDumb(this.project)) false
    else if (this.classReference !is PhpTypedElement) false
    else PhpIndex.getInstance(this.project)
        .completeType(this.project, (this.classReference as PhpTypedElement).type, mutableSetOf())
        .types
        .flatMap { PhpIndex.getInstance(this.project).getClassesByFQN(it) }
        .flatMap { it.superClasses.map { parent -> parent.fqn } + it.fqn }
        .containsAny(*classes)
