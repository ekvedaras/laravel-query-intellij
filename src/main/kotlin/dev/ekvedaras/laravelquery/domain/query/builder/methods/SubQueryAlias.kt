package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.v4.completion.DeclarativeInsertHandler
import icons.DatabaseIcons

data class SubQueryAlias(override val name: String, override val definitionParameter: StringParameter) : Alias {
    override fun asLookupElement(): LookupElement =
        LookupElementBuilder
            .createWithSmartPointer(name, definitionParameter.element.originalElement)
            .withIcon(DatabaseIcons.Synonym)
            .withInsertHandler(
                DeclarativeInsertHandler.Builder()
                    .disableOnCompletionChars(".")
                    .insertOrMove(".")
                    .triggerAutoPopup()
                    .build()
            )
}
