package dev.ekvedaras.laravelquery.support

import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType

inline fun <reified T : PsiElement> PsiElement.callChainOfType(): Set<T> {
    val chain = mutableSetOf<T>()

    var element = when (this) {
        is T -> this
        else -> this.firstChild
    }

    while (element is T) {
        chain.add(element)
        element = element.firstChild
    }

    return chain.toSet()
}

inline fun <reified T : PsiElement> PsiElement.firstChildOfType(): T? = childrenOfType<T>().firstOrNull()
