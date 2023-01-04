package dev.ekvedaras.laravelquery.v4.reference

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForTableByName
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isEloquentModel
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInteresting
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isTableParam
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.shouldCompleteOnlyColumns
import dev.ekvedaras.laravelquery.utils.MethodUtils

class TableOrViewReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY
        val project = method.project

        if (shouldNotInspect(project, method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        return arrayOf(
            SchemaPsiReference(element, DbReferenceExpression.Companion.Type.Table),
            TableOrViewPsiReference(element, DbReferenceExpression.Companion.Type.Table),
        )
    }

    private fun shouldNotInspect(project: Project, method: MethodReference, element: PsiElement) =
        !ApplicationManager.getApplication().isReadAccessAllowed ||
            !method.isBuilderMethodForTableByName() ||
            !element.isTableParam() ||
            element.isInsideRegularFunction() ||
            (method.isEloquentModel(project) && method.shouldCompleteOnlyColumns()) ||
            !method.isInteresting(project)
}
