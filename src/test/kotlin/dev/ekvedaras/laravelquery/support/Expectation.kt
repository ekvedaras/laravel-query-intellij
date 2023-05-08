package dev.ekvedaras.laravelquery.support

sealed interface Expectation<T> {
    val not: T
    val but: T
    val and: T
}
