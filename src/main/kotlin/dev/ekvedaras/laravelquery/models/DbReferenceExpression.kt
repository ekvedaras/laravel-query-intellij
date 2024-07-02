package dev.ekvedaras.laravelquery.models

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasForeignKey
import com.intellij.database.model.DasIndex
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.model.DasTableKey
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiTreeChangeAdapter
import com.intellij.psi.PsiTreeChangeEvent
import dev.ekvedaras.laravelquery.utils.DbReferenceResolver
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup
import dev.ekvedaras.laravelquery.utils.TableAndAliasCollector
import kotlin.math.exp
import org.apache.commons.lang.StringUtils.substringBefore

class DbReferenceExpression(val expression: PsiElement, val type: Type) {
    companion object {
        enum class Type {
            Table,
            Column,
            Index,
            Key,
            ForeignKey,
        }
    }

    val project: Project = expression.project

    val tablesAndAliases = mutableMapOf<String, Pair<String, String?>>()
    val aliases = mutableMapOf<String, Pair<String, PsiElement>>()

    var schema = mutableListOf<DasNamespace>()
    var table = mutableListOf<DasTable>()
    var column = mutableListOf<DasColumn>()
    var index = mutableListOf<DasIndex>()
    var key = mutableListOf<DasTableKey>()
    var foreignKey = mutableListOf<DasForeignKey>()
    var alias: String? = null

    val parts = mutableListOf<String>()
    val ranges = mutableListOf<TextRange>()

    init {
        parts.addAll(
            expression.text.unquoteAndCleanup()
                .substringBefore("->") // strip out json fields
                .split(".")
                .map { it.substringBefore(" as").substringBefore(" AS").trim() }
        )

        for (part in parts) {
            ranges.add(TextRange.from(if (ranges.isNotEmpty()) ranges.last().endOffset + 1 else 1, part.length))
        }

        if (!DumbService.isDumb(project)) {
            val expressionDisposable = Disposer.newDisposable()
            PsiManager.getInstance(project).addPsiTreeChangeListener(object : PsiTreeChangeAdapter() {
                override fun childrenChanged(event: PsiTreeChangeEvent) {
                    expressionDisposable.dispose()
                }
            }, expressionDisposable)
            ReadAction.nonBlocking<Unit> {
                try {
                    TableAndAliasCollector(this).collect()
                    DbReferenceResolver(this).resolve()
                } catch (_: ProcessCanceledException) {

                }
            }.inSmartMode(project).expireWith(expressionDisposable).executeSynchronously()
        }
    }
}
