package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isBuilderClassMethod
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isBuilderMethodByName
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isTableParam
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class TableOrViewReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY
        val project = method.project

        if (shouldNotInspect(project, method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        val target = DbReferenceExpression(element, DbReferenceExpression.Companion.Type.Table)
        var references = arrayOf<PsiReference>()

        target.schema.parallelStream().forEach { references += SchemaPsiReference(target, it) }
        target.table.parallelStream().forEach { references += TableOrViewPsiReference(target, it) }

        return references
    }

    private fun shouldNotInspect(project: Project, method: MethodReference, element: PsiElement) =
        !method.isBuilderMethodByName() ||
            !element.isTableParam() ||
            element.isInsideRegularFunction() ||
            !method.isBuilderClassMethod(project)
}
