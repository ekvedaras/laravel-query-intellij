package dev.ekvedaras.laravelquery.reference

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.rd.util.addUnique
import com.jetbrains.rd.util.lifetime.Lifetime
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodByName
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isEloquentModel
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInteresting
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isTableParam
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.shouldCompleteOnlyColumns
import dev.ekvedaras.laravelquery.utils.MethodUtils

class TableOrViewReferenceProvider : PsiReferenceProvider() {
    companion object {
        val isResolving = mutableListOf<PsiElement>()
    }

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (isResolving.contains(element)) {
            return PsiReference.EMPTY_ARRAY
        }

        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY
        val project = method.project

        if (shouldNotInspect(project, method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        isResolving.addUnique(Lifetime.Eternal, element)

        val target = DbReferenceExpression(element, DbReferenceExpression.Companion.Type.Table)
        var references = arrayOf<PsiReference>()

        target.schema.parallelStream().forEach { references += SchemaPsiReference(target, it) }
        target.table.parallelStream().forEach { references += TableOrViewPsiReference(target, it) }

        isResolving.remove(element)

        return references
    }

    private fun shouldNotInspect(project: Project, method: MethodReference, element: PsiElement) =
        !ApplicationManager.getApplication().isReadAccessAllowed ||
            !method.isBuilderMethodByName() ||
            !element.isTableParam() ||
            element.isInsideRegularFunction() ||
            (method.isEloquentModel(project) && method.shouldCompleteOnlyColumns()) ||
            !method.isInteresting(project)
}
