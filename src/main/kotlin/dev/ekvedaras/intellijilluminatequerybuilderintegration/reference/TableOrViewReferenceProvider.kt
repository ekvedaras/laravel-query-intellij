package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class TableOrViewReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY

        if (shouldNotCompleteCurrentParam(method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        if (!LaravelUtils.isQueryBuilderMethod(method)) {
            return PsiReference.EMPTY_ARRAY
        }

        val target = DbReferenceExpression(element, DbReferenceExpression.Companion.Type.Table)
        var references = arrayOf<PsiReference>()

        if (target.schema != null && (element.text.split(".").size == 2 || target.schema!!.name == target.parts[0])) {
            references += SchemaPsiReference(element, target.schema!!)
        }

        if (target.table != null) {
            references += TableOrViewPsiReference(element, target.table!!)
        }

//        DbUtil.getDataSources(element.project).forEach { dataSource ->
//            DasUtil.getTables(dataSource.dataSource).forEach {
//                if (!it.isSystem && it.name == element.text.substringBefore(" as ").trim('"').trim('\'')) {
//                    references += TableOrViewPsiReference(element, it)
//                }
//            }
//        }

        return references
    }

    private fun shouldNotCompleteCurrentParam(method: MethodReference, element: PsiElement) =
        !LaravelUtils.BuilderTableMethods.contains(method.name)
                || MethodUtils.findParameterIndex(element) != 0
}