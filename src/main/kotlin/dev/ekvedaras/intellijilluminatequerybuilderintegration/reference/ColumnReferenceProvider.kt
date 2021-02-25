package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.elementType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class ColumnReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY
        val project = method.project

        if (shouldNotCompleteCurrentParameter(method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        if (shouldNotCompleteArrayValue(method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        if (!LaravelUtils.isQueryBuilderMethod(method, project)) {
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

    private fun shouldNotCompleteCurrentParameter(method: MethodReference, element: PsiElement) =
        element.textContains('$') ||
            !LaravelUtils.BuilderTableColumnsParams.containsKey(method.name) ||
            (
                !LaravelUtils.BuilderTableColumnsParams[method.name]!!.contains(MethodUtils.findParameterIndex(element)) &&
                    !LaravelUtils.BuilderTableColumnsParams[method.name]!!.contains(-1)
                ) ||
            (element.parent?.parent?.parent is FunctionReference && element.parent?.parent?.parent !is MethodReference)

    private fun shouldNotCompleteArrayValue(method: MethodReference, element: PsiElement) =
        !LaravelUtils.BuilderMethodsWithTableColumnsInArrayValues.contains(method.name) &&
            (
                element.parent.parent.elementType?.index?.toInt() == 1889 || // 1889 - array expression
                    element.parent.parent.elementType?.index?.toInt() == 805
                ) // 805 - array value
}
