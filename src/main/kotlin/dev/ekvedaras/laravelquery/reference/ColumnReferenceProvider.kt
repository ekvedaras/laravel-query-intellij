package dev.ekvedaras.laravelquery.reference

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.canHaveColumnsInArrayValues
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForColumns
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isColumnIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInteresting
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.shouldCompleteOnlyColumns
import dev.ekvedaras.laravelquery.utils.MethodUtils
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.containsVariable
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.isArrayValue

class ColumnReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY
        val project = method.project

        if (shouldNotInspect(project, method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        var references = arrayOf<PsiReference>()

        if (!method.shouldCompleteOnlyColumns()) {
            references += SchemaPsiReference(element, DbReferenceExpression.Companion.Type.Column)
            references += TableOrViewPsiReference(element, DbReferenceExpression.Companion.Type.Column)
        }

        references += ColumnPsiReference(element)

        return references
    }

    private fun shouldNotInspect(project: Project, method: MethodReference, element: PsiElement): Boolean {
        val allowArray = method.name?.startsWith("where") ?: false

        return !ApplicationManager.getApplication().isReadAccessAllowed ||
            element.containsVariable() ||
            !method.isBuilderMethodForColumns() ||
            !element.isColumnIn(method, allowArray) ||
            element.isInsideRegularFunction() ||
            ((element.parent?.isArrayValue() ?: false) && !method.canHaveColumnsInArrayValues()) ||
            !method.isInteresting(project)
    }
}
