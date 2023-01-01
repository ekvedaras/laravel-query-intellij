package dev.ekvedaras.laravelquery.support

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.descendantsOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpPsiElement

inline fun <reified T : PsiElement> PsiElement.descendantsOfType(): Set<T> {
    val descendants = PsiTreeUtil.getChildrenOfTypeAsList(this, T::class.java).toMutableSet()

    descendants.forEach { descendants += it.descendantsOfType<T>() }

    return descendants
}

inline fun <reified T: PsiElement> ArrayCreationExpression.elementsOfType(): Set<T> =
    this.childrenOfType<PhpPsiElement>().filter { it.firstPsiChild is T }.map { it.firstPsiChild as T }.toSet()
