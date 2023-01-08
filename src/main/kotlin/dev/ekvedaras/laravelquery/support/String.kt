package dev.ekvedaras.laravelquery.support

import dev.ekvedaras.laravelquery.v4.utils.PsiUtils.Companion.unquoteAndCleanup

fun String.substringBefore(delimiter: String, missingDelimiterValue: String = this, ignoreCase: Boolean = false): String {
    val index = indexOf(delimiter, ignoreCase = ignoreCase)
    return if (index == -1) missingDelimiterValue else substring(0, index)
}

fun String.substringAfter(delimiter: String, missingDelimiterValue: String = this, ignoreCase: Boolean = false): String {
    val index = indexOf(delimiter, ignoreCase = ignoreCase)
    return if (index == -1) missingDelimiterValue else substring(index + delimiter.length, length)
}

fun String.cleanup(): String = this.replace("IntellijIdeaRulezzz", "").trim('\'', '"').trim()
