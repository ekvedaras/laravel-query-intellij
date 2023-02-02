package dev.ekvedaras.laravelquery.support

import java.util.function.Predicate
import java.util.stream.Stream

fun <T> Stream<T>.firstWhereOrNull(predicate: Predicate<in T>): T? =
    filter(predicate)
        .findFirst()
        .orElseGet { null }
