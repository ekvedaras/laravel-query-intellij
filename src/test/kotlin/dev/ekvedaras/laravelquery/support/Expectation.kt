package dev.ekvedaras.laravelquery.support

sealed interface Expectation<T> {
    fun not(): T
    fun but(): T
    fun and(): T
}
