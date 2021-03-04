package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.canHaveColumnsInArrayValues
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isBuilderClassMethod
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isBuilderMethodForColumns
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isColumnIn
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isInsidePhpArrayOrValue
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.PsiUtils.Companion.containsVariable

class ColumnReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY
        val project = method.project

        if (shouldNotInspect(method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        if (element.isInsidePhpArrayOrValue() && !method.canHaveColumnsInArrayValues()) {
            return PsiReference.EMPTY_ARRAY
        }

        if (!method.isBuilderClassMethod(project)) {
            return PsiReference.EMPTY_ARRAY
        }

        val target = DbReferenceExpression(element, DbReferenceExpression.Companion.Type.Column)
        var references = arrayOf<PsiReference>()

        target.schema.parallelStream().forEach { references += SchemaPsiReference(target, it) }
        target.table.parallelStream().forEach {
            references += TableOrViewPsiReference(target, it)

            if (target.aliases.containsKey(it.name)) {
                references += TableAliasPsiReference(
                    element,
                    if (target.ranges.size >= 2 && target.schema.isNotEmpty()) target.ranges[1] else target.ranges.first(),
                    target.aliases[it.name]!!.second
                )
            }
        }
        target.column.parallelStream().forEach { references += ColumnPsiReference(target, it) }

        return references
    }

    private fun shouldNotInspect(method: MethodReference, element: PsiElement) =
        element.containsVariable()
                || !method.isBuilderMethodForColumns()
                || !element.isColumnIn(method)
                || element.isInsideRegularFunction()
}
