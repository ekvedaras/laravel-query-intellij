package dev.ekvedaras.laravelquery.integration.query.reference

import com.intellij.database.psi.DbTable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReferenceBase
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.ReferencesTable

class TableReference(private val stringParameter: StringParameter, rangeInElement: TextRange) : PsiReferenceBase<StringLiteralExpression>(stringParameter.element, rangeInElement) {
    override fun resolve(): DbTable? {
        val methodCall = stringParameter.queryMethodCall ?: return null
        if (methodCall !is ReferencesTable) return null

        return methodCall.findTableReferencedIn(stringParameter)
    }
}
