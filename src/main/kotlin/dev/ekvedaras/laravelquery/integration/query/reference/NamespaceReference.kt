package dev.ekvedaras.laravelquery.integration.query.reference

import com.intellij.database.psi.DbNamespace
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReferenceBase
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.ReferencesNamespace

class NamespaceReference(private val stringParameter: StringParameter, rangeInElement: TextRange) : PsiReferenceBase<StringLiteralExpression>(stringParameter.element, rangeInElement) {
    override fun resolve(): DbNamespace? {
        val methodCall = stringParameter.queryMethodCall ?: return null
        if (methodCall !is ReferencesNamespace) return null

        return methodCall.findNamespaceReferencedIn(stringParameter)
    }
}
