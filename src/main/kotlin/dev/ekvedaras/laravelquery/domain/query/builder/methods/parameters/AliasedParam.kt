package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.support.substringAfter
import dev.ekvedaras.laravelquery.support.substringBefore

data class AliasedParam(private val value: StringParameter) {
    val target: String = value.text.substringBefore(" as ", ignoreCase = true).trim()
    val alias: String? = if (value.text.contains(" as ", ignoreCase = true)) {
            value.text.substringAfter(" as ", ignoreCase = true).trim()
        } else {
            null
        }
}
