package dev.ekvedaras.laravelquery.support

import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.PhpPsiElement

inline fun <reified T : PsiElement> ArrayCreationExpression.hashKeysOfType(): Set<T> = this
    .hashElements
    .map { it.key }
    .filterIsInstance<T>()
    .toSet()

inline fun <reified T : PsiElement> ArrayCreationExpression.nonHashEntriesOfType(): Set<T> = this
    .childrenOfType<PhpPsiElement>()
    .asSequence()
    .filterNot { it is ArrayHashElement }
    .map { it.firstPsiChild }
    .filterIsInstance<T>()
    .toSet()

inline fun <reified T : PsiElement> ArrayCreationExpression.hashKeysOrEntriesOfType(): Set<T> = hashKeysOfType<T>() + nonHashEntriesOfType()

inline fun <reified T : PsiElement> ArrayCreationExpression.elementsOfType(): Set<T> =
    childrenOfType<PhpPsiElement>().filter { it.firstPsiChild is T }.map { it.firstPsiChild as T }.toSet()
