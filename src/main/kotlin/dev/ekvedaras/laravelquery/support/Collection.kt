package dev.ekvedaras.laravelquery.support

fun <T> Collection<T>.containsAny(vararg elements: T): Boolean = elements.firstOrNull { this.contains(it) } != null
