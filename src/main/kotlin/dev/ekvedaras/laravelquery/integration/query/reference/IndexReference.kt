package dev.ekvedaras.laravelquery.integration.query.reference

import com.intellij.database.psi.DbIndex
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReferenceBase
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.ReferencesIndex
import dev.ekvedaras.laravelquery.domain.StringParameter

class IndexReference(private val stringParameter: StringParameter, rangeInElement: TextRange) : PsiReferenceBase<StringLiteralExpression>(stringParameter.element, rangeInElement) {
    override fun resolve(): DbIndex? {
        val methodCall = stringParameter.queryMethodCall ?: return null
        if (methodCall !is ReferencesIndex) return null

        return methodCall.findIndexReferencedIn(stringParameter)
    }
}
