package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.lexer.PhpTokenTypes

class TableOrViewReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(),
//            PlatformPatterns.or(
//                PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL),
//                PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL_SINGLE_QUOTE)
//            ),
            TableOrViewReferenceProvider()
        )
    }
}