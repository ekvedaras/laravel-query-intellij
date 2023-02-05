package dev.ekvedaras.laravelquery.support

inline fun <T : Any, R> T?.transform(using: (T) -> R) = let {
    if (it != null) using(it) else null
}

inline fun <T : Any, R> T?.tryTransforming(using: (T) -> R) = let {
    try { it.transform(using) } catch (e: Exception) { null}
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
