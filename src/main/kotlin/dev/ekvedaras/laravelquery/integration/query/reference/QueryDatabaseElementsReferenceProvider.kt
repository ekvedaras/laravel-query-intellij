package dev.ekvedaras.laravelquery.integration.query.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.ColumnSelectionCall
import dev.ekvedaras.laravelquery.domain.query.builder.methods.TableSelectionCall
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class QueryDatabaseElementsReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val string = element.transformInstanceOf<StringLiteralExpression, StringParameter> {
            StringParameter(it)
        } ?: return PsiReference.EMPTY_ARRAY

        if (! string.shouldBeInspected()) return PsiReference.EMPTY_ARRAY

        val methodCall = string.queryMethodCall ?: return PsiReference.EMPTY_ARRAY

        var references = arrayOf<PsiReference>()

        if (methodCall is TableSelectionCall) {
            references += TableReference(string, string.lastPartRange)
            if (string.hasTwoParts) references += NamespaceReference(string, string.oneBeforeLastPartRange)
        } else if (methodCall is ColumnSelectionCall) {
            methodCall.columns.forEach {
                if (it.stringParameter.parts.isNotEmpty()) {
                    references += ColumnReference(it.stringParameter, it.stringParameter.lastPartRange)
                }

                if (it.stringParameter.parts.size > 1) {
                    references += TableReference(it.stringParameter, it.stringParameter.oneBeforeLastPartRange)
                }

                if (it.stringParameter.parts.size > 2) {
                    references += NamespaceReference(it.stringParameter, it.stringParameter.twoBeforeLastPartRange)
                }
            }
        }

        return references
    }
}
