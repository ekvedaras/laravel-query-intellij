package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.database.Namespace

data class NamespaceParameter(val stringParameter: StringParameter) {
    val namespaceName = stringParameter.text
    val namespace: Namespace? = Namespace.findFirst(namespaceName, stringParameter.project)

    fun getCompletionOptions(): List<LookupElement> =
        Namespace.list(stringParameter.project).map { it.asLookupElement() }.toList()
}
