package dev.ekvedaras.laravelquery.integration.schema.blueprint.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.ReferencesIndex
import dev.ekvedaras.laravelquery.domain.ReferencesTableKey
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.StringParameter.Companion.asStringParameter
import dev.ekvedaras.laravelquery.support.transformInstanceOf

class SchemaDatabaseElementsReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val string = element.transformInstanceOf<StringLiteralExpression, StringParameter> {
            it.asStringParameter()
        } ?: return PsiReference.EMPTY_ARRAY

        if (!string.shouldBeInspected()) return PsiReference.EMPTY_ARRAY

        val methodCall = string.blueprintMethodCall ?: return PsiReference.EMPTY_ARRAY

        if (methodCall is ReferencesIndex) return arrayOf(
            IndexReference(string, string.lastPartRange),
        )

        if (methodCall is ReferencesTableKey) return arrayOf(
            TableKeyReference(string, string.lastPartRange),
        )

        // TODO: column and table references

        return arrayOf()
    }
}
