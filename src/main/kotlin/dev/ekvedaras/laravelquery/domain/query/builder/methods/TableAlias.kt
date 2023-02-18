package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.v4.completion.DeclarativeInsertHandler
import icons.DatabaseIcons

data class TableAlias(override val name: String, override val definitionParameter: StringParameter, val table: Table) : Alias {
    override fun asLookupElement(): LookupElement =
        LookupElementBuilder
            .createWithSmartPointer(name, definitionParameter.element.originalElement)
            .withIcon(DatabaseIcons.Synonym)
            .withTailText("(${table.name})", true)
            .withTypeText(table.namespace.dataSource.name, true)
            .withInsertHandler(
                DeclarativeInsertHandler.Builder()
                    .disableOnCompletionChars(".")
                    .insertOrMove(".")
                    .triggerAutoPopup()
                    .build()
            )
}
