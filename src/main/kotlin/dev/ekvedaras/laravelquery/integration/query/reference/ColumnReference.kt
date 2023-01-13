package dev.ekvedaras.laravelquery.integration.query.reference

import com.intellij.database.psi.DbColumn
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReferenceBase
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.builder.methods.ReferencesColumn

class ColumnReference(private val stringParameter: StringParameter, rangeInElement: TextRange) : PsiReferenceBase<StringLiteralExpression>(stringParameter.element, rangeInElement) {
    override fun resolve(): DbColumn? {
        val methodCall = stringParameter.queryMethodCall ?: return null
        if (methodCall !is ReferencesColumn) return null

        return methodCall.findColumnReferencedIn(stringParameter)
    }
}
