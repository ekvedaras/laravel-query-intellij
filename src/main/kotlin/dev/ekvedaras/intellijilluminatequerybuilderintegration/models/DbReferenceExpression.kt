package dev.ekvedaras.intellijilluminatequerybuilderintegration.models

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DbReferenceResolver
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.TableAndAliasCollector

class DbReferenceExpression(val expression: PsiElement, val type: Type) {
    companion object {
        enum class Type {
            Table,
            Column
        }
    }

    val project : Project = expression.project

    val tablesAndAliases = mutableMapOf<String, Pair<String, String?>>()
    val aliases = mutableMapOf<String, Pair<String, PsiElement>>()

    var schema = mutableListOf<DasNamespace>()
    var table = mutableListOf<DasTable>()
    var column = mutableListOf<DasColumn>()
    var alias: String? = null

    val parts = mutableListOf<String>()
    val ranges = mutableListOf<TextRange>()

    init {
        parts.addAll(
            expression.text
                .trim('"')
                .trim('\'')
                .split(".")
                .map { it.replace("IntellijIdeaRulezzz", "").substringBefore(" as").trim() }
        )

        for (part in parts) {
            ranges.add(TextRange.from(if (ranges.isNotEmpty()) ranges.last().endOffset + 1 else 1, part.length))
        }

        if (type == Type.Column) TableAndAliasCollector(this).collect()

        DbReferenceResolver(this).resolve()
    }
}
