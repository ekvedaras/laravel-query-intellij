package dev.ekvedaras.laravelquery.support

import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType

inline fun <reified T : PsiElement> PsiElement.callChainOfType(): Set<T> {
    val chain = mutableSetOf<T>()

    var element = when (this) {
        is T -> this
        else -> firstChild
    }

    while (element is T) {
        chain.add(element)
        element = element.firstChild
    }

    /**
     * In case of `$query->from('users')->where('id', 1)->get();`,
     * the chain would be: get, where, from due to nested structure being used in PSI tree.
     * Therefore, were return this list as reversed to make it easier to reason about.
     */
    return chain.reversed().toSet()
}

inline fun <reified T : PsiElement> PsiElement.firstChildOfType(): T? = childrenOfType<T>().firstOrNull()
