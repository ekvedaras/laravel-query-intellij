package dev.ekvedaras.laravelquery.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DbUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl
import com.jetbrains.rd.util.first
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import dev.ekvedaras.laravelquery.utils.BlueprintMethod.Companion.getColumnDefinitionReference
import dev.ekvedaras.laravelquery.utils.BlueprintMethod.Companion.getColumnName
import dev.ekvedaras.laravelquery.utils.BlueprintMethod.Companion.isColumnDefinition
import dev.ekvedaras.laravelquery.utils.BlueprintMethod.Companion.isId
import dev.ekvedaras.laravelquery.utils.BlueprintMethod.Companion.isSoftDeletes
import dev.ekvedaras.laravelquery.utils.BlueprintMethod.Companion.isTimestamps
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.columnsInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.tables
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBlueprintMethod
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.MethodUtils
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.references
import java.util.Collections

class NewMigrationCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val method = MethodUtils.resolveMethodReference(parameters.position) ?: return
        val project = method.project

        if (shouldNotComplete(project, method, parameters)) {
            return
        }

        val target = DbReferenceExpression(parameters.position, DbReferenceExpression.Companion.Type.Column)
        val items = Collections.synchronizedList(mutableListOf<LookupElement>())

        var table: DasTable? = null;

        DbUtil.getDataSources(project).filter {
            LaravelQuerySettings.getInstance(project).interestedIn(it)
        }.forEach { dataSource ->
            val dasTable = dataSource.tables().firstOrNull { it.name == target.tablesAndAliases.first().key }
            if (dasTable != null) {
                table = dasTable
                return@forEach
            }
        }

        val columns = table?.getDasChildren(ObjectKind.COLUMN)?.map { it.name } ?: listOf<String>()

        if (ApplicationManager.getApplication().isReadAccessAllowed) {
            ApplicationManager.getApplication().runReadAction {
                (method.firstPsiChild as VariableImpl).references().forEach { reference ->
                    val referenceMethod = (reference.element as VariableImpl).parent as MethodReferenceImpl

                    if (referenceMethod == method) {
                        return@forEach
                    }

                    if (referenceMethod.isId() && !columns.contains("id")) {
                        items.add(
                            LookupElementBuilder
                                .create("id")
                                .withPsiElement(referenceMethod)
                        )
                    } else if (referenceMethod.isTimestamps()) {
                        if (!columns.contains("created_at")) {
                            items.add(
                                LookupElementBuilder
                                    .create("created_at")
                                    .withPsiElement(referenceMethod)
                            )
                        }

                        if (!columns.contains("updated_at")) {
                            items.add(
                                LookupElementBuilder
                                    .create("updated_at")
                                    .withPsiElement(referenceMethod)
                            )
                        }
                    } else if (referenceMethod.isSoftDeletes() && !columns.contains("deleted_at")) {
                        items.add(
                            LookupElementBuilder
                                .create("deleted_at")
                                .withPsiElement(referenceMethod)
                        )
                    } else if (referenceMethod.isColumnDefinition() && !columns.contains(referenceMethod.getColumnName())) {
                        items.add(
                            LookupElementBuilder
                                .create(referenceMethod.getColumnName() ?: '?')
                                .withPsiElement(referenceMethod.getColumnDefinitionReference())
                        )
                    }
                }
            }
        }

        result.addAllElements(
            items.distinctBy { it.lookupString }
        )

        result.stopHere()
    }

    private fun shouldNotComplete(project: Project, method: MethodReference, parameters: CompletionParameters) =
        !ApplicationManager.getApplication().isReadAccessAllowed ||
            !method.isBlueprintMethod(project) ||
            parameters.isInsideRegularFunction() ||
            method.firstPsiChild !is VariableImpl
}
