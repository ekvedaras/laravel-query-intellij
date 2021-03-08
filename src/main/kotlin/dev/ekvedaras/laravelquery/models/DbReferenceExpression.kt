package dev.ekvedaras.laravelquery.models

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import dev.ekvedaras.laravelquery.utils.DbReferenceResolver
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup
import dev.ekvedaras.laravelquery.utils.TableAndAliasCollector

class DbReferenceExpression(val expression: PsiElement, val type: Type, val resolveAliases: Boolean = true) {
    companion object {
        enum class Type {
            Table,
            Column,
        }
    }

    val project: Project = expression.project

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
            expression.text.unquoteAndCleanup()
                .split(".")
                .map { it.substringBefore(" as").trim() }
        )

        for (part in parts) {
            ranges.add(TextRange.from(if (ranges.isNotEmpty()) ranges.last().endOffset + 1 else 1, part.length))
        }

        if (!DumbService.isDumb(project) && ApplicationManager.getApplication().isReadAccessAllowed) {
            ApplicationManager.getApplication().runReadAction {
                if (resolveAliases) TableAndAliasCollector(this).collect()

                DbReferenceResolver(this).resolve()
            }
        }
    }
}
