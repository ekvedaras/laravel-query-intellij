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
