package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import kotlin.streams.toList

class RelationParameter(val stringParameter: StringParameter) {
    val name: String = stringParameter.text

    fun getCompletionOptions(model: Model?): List<LookupElement> =
        model?.relations?.map { it.asLookupElement() } ?: listOf()
}
