package dev.ekvedaras.laravelquery.integration.query.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl

class QueryDatabaseElementsReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpressionImpl::class.java),
            QueryDatabaseElementsReferenceProvider(),
        )
    }
}
