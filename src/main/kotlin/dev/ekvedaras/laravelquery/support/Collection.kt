package dev.ekvedaras.laravelquery.support

fun <T> Collection<T>.containsAny(vararg elements: T): Boolean = elements.firstOrNull { contains(it) } != null
