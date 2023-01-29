package dev.ekvedaras.laravelquery.integration.query.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.ColumnSelectionCall
import dev.ekvedaras.laravelquery.domain.query.builder.methods.TableSelectionCall
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class QueryDatabaseElementsReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val string = element.transformInstanceOf<StringLiteralExpression, StringParameter> {
            it.asStringParameter()
        } ?: return PsiReference.EMPTY_ARRAY

        if (!string.shouldBeInspected()) return PsiReference.EMPTY_ARRAY

        val methodCall = string.queryMethodCall ?: return PsiReference.EMPTY_ARRAY

        if (methodCall is TableSelectionCall && methodCall.tableParameter?.stringParameter == string) return arrayOf(
            TableReference(string, string.lastPartRange),
            NamespaceReference(string, string.oneBeforeLastPartRange)
        )

        if (methodCall is ColumnSelectionCall) return arrayOf(
            ColumnReference(string, string.lastPartRange),
            TableReference(string, string.oneBeforeLastPartRange),
            NamespaceReference(string, string.twoBeforeLastPartRange)
        )

        return arrayOf()
    }
}
