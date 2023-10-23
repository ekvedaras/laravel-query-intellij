package dev.ekvedaras.laravelquery.integration.schema.blueprint.reference

import com.intellij.database.psi.DbTableKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReferenceBase
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.ReferencesTableKey
import dev.ekvedaras.laravelquery.domain.StringParameter

class TableKeyReference(private val stringParameter: StringParameter, rangeInElement: TextRange) : PsiReferenceBase<StringLiteralExpression>(stringParameter.element, rangeInElement) {
    override fun resolve(): DbTableKey? {
        val methodCall = stringParameter.blueprintMethodCall ?: return null
        if (methodCall !is ReferencesTableKey) return null

        return methodCall.findTableKeyReferencedIn(stringParameter)
    }
}
