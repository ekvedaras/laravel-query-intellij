package dev.ekvedaras.laravelquery.integration.query.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.lexer.PhpTokenTypes

class DatabaseElementsCompletionContributor : CompletionContributor() {
    @Deprecated("Overriden function is deprecated, but no clear replacement for this feature available")
    override fun invokeAutoPopup(position: PsiElement, typeChar: Char): Boolean {
        return typeChar == '\'' || typeChar == '"'
    }

    init {
        val stringLiteralPattern = PlatformPatterns.or(
            PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL_SINGLE_QUOTE),
            PlatformPatterns.psiElement(PhpTokenTypes.STRING_LITERAL),
        )

        extend(CompletionType.BASIC, stringLiteralPattern, NamespaceCompletionProvider())
        extend(CompletionType.BASIC, stringLiteralPattern, TableCompletionProvider())
        extend(CompletionType.BASIC, stringLiteralPattern, ColumnCompletionProvider())
    }
}
