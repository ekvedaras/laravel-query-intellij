package dev.ekvedaras.laravelquery.utils

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import java.util.stream.Stream

object ElementTypes {
    const val PhpArray = 1889
    const val ArrayValue = 805
}

class PsiUtils private constructor() {
    companion object {
        fun PsiElement.containsVariable(): Boolean = this.textContains('$')
        fun CompletionParameters.containsVariable(): Boolean = this.position.containsVariable()
        fun String.containsAlias(): Boolean = this.contains(" as ")
        fun PsiElement.isPhpArray(): Boolean = this.typeAsInt() == ElementTypes.PhpArray
        fun PsiElement.isArrayValue(): Boolean = this.typeAsInt() == ElementTypes.ArrayValue
        fun String.unquoteAndCleanup() = this.replace("IntellijIdeaRulezzz", "").trim('\'', '"').trim()
        fun Variable.referencesInParallel(): Stream<out PsiReference> =
            ReferencesSearch.search(this.originalElement).findAll().parallelStream()

        fun PsiReference.statementFirstPsiChild(): PsiElement? =
            if (ApplicationManager.getApplication().isReadAccessAllowed) {
                this.element.parentOfType<Statement>()?.firstPsiChild
            } else {
                null
            }
        private fun PsiElement.typeAsInt(): Int = this.elementType?.index?.toInt() ?: 0
    }
}
