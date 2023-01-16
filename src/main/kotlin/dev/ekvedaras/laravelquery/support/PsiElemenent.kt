package dev.ekvedaras.laravelquery.support

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.descendantsOfType

inline fun <reified T : PsiElement> PsiElement.descendantsOfType(): Set<T> {
    val descendants = PsiTreeUtil.getChildrenOfTypeAsList(this, T::class.java).toMutableSet()

    descendants.forEach { descendants += it.descendantsOfType<T>() }

    return descendants
}
