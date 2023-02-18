package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.StringParameter

interface Alias {
    val name: String
    val definitionParameter: StringParameter
    fun asLookupElement(): LookupElement
}
