package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class ColumnReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY

        if (shouldNotCompleteCurrentParameter(method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        if (shouldNotCompleteArrayValue(method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        if (!LaravelUtils.isQueryBuilderMethod(method)) {
            return PsiReference.EMPTY_ARRAY
        }

        val target = DbReferenceExpression(element, DbReferenceExpression.Companion.Type.Column)
        var references = arrayOf<PsiReference>()

        target.schema.forEach { references += SchemaPsiReference(target, it) }
        target.table.forEach { references += TableOrViewPsiReference(target, it) }
        target.column.forEach { references += ColumnPsiReference(target, it) }

        return references
    }

    private fun shouldNotCompleteCurrentParameter(method: MethodReference, element: PsiElement) =
        LaravelUtils.BuilderTableColumnsParams[method.name]?.contains(
            MethodUtils.findParameterIndex(element)
        ) != true

    private fun shouldNotCompleteArrayValue(method: MethodReference, element: PsiElement) =
        !LaravelUtils.BuilderMethodsWithTableColumnsInArrayValues.contains(method.name)
                && element.parent.parent.elementType?.index?.toInt() == 1889
}