package dev.ekvedaras.laravelquery.integration.query.reference

import com.intellij.database.psi.DbNamespace
import com.intellij.database.psi.DbTable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.domain.query.builder.methods.ReferencesNamespace
import dev.ekvedaras.laravelquery.domain.query.builder.methods.ReferencesTable

class NamespaceReference(private val stringParameter: StringParameter, rangeInElement: TextRange) : PsiReferenceBase<StringLiteralExpression>(stringParameter.element, rangeInElement) {
    override fun resolve(): DbNamespace? {
        val methodCall = stringParameter.methodCall ?: return null
        if (methodCall !is ReferencesNamespace) return null

        return methodCall.findNamespaceReferencedIn(stringParameter)
    }
}
