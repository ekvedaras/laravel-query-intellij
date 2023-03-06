package dev.ekvedaras.laravelquery.support

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

inline fun <T : Any, R> T?.transform(using: (T) -> R) = let {
    if (it != null) using(it) else null
}

inline fun <T : Any, R> T?.tryTransforming(using: (T) -> R) = let {
    try {
        it.transform(using)
    } catch (e: Exception) {
        null
    }
}

inline fun <reified TFrom, TTo> Any?.transformInstanceOf(using: (TFrom) -> TTo): TTo? = let {
    (it as? TFrom).transform(using)
}

inline fun <T : Any> T?.tap(using: (T) -> Unit): T? = also {
    if (it != null) using(it)
}

inline fun <reified TFrom, TTo> Any?.tryTransformingInstanceOfOrContinue(using: (TFrom) -> TTo) = let {
    if (it !is TFrom) return it

    try {
        it.transform(using)
    } catch (e: Exception) {
        it
    }
}

inline fun <reified TPassThrough, reified TFrom, TTo : TPassThrough> Any?.tryTransformingInstanceOfUnless(using: (TFrom) -> TTo): TPassThrough? = let {
    if (it is TPassThrough) return it
    if (it !is TFrom) return null

    try {
        it.transform(using)
    } catch (e: Exception) {
        null
    }
}

fun <R> returnWhen(condition: Boolean, action: () -> R): R? = if (condition) action() else null
fun <R> returnWhen(condition: Boolean, result: R): R? = if (condition) result else null

fun <R> returnUnless(condition: Boolean, action: () -> R): R? = returnWhen(!condition, action)
fun <R> returnUnless(condition: Boolean, result: R): R? = returnWhen(!condition, result)

fun <R> Project.whenSmart(action: () -> R): R? = returnUnless(DumbService.isDumb(this), action)

fun <R> PsiElement.whenSmart(action: () -> R): R? = project.whenSmart(action)
