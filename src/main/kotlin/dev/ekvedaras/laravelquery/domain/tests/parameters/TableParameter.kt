package dev.ekvedaras.laravelquery.domain.tests.parameters

import com.intellij.codeInsight.lookup.LookupElement
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.database.Namespace
import dev.ekvedaras.laravelquery.domain.database.Table
import kotlin.streams.toList

class TableParameter(val stringParameter: StringParameter) {
    private val tableName: String
    private val namespaceName: String?

    init {
        val parts = stringParameter.text.split('.').reversed()

        tableName = parts[0]
        namespaceName = parts.getOrNull(1)
    }

    val namespace: Namespace? = if (namespaceName != null) {
        Namespace.findFirst(namespaceName, stringParameter.project)
    } else {
        Table.findFirst(tableName, stringParameter.project)?.namespace
    }

    val table: Table? = namespace?.findTable(tableName) ?: Table.findFirst(tableName, stringParameter.project)

    fun getCompletionOptions(): List<LookupElement> {
        val completion = mutableListOf<LookupElement>()

        if (stringParameter.isEmpty) {
            completion += Namespace.list(stringParameter.project).map { it.asLookupElement() }.toList()
            completion += Table.list(stringParameter.project).map { it.asLookupElement() }.toList()
        } else if (stringParameter.hasUncompletedPart && namespace != null) {
            completion += namespace
                .tables()
                .map { it.asLookupElement() }
                .toList()
        }

        return completion
    }
}
