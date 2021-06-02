package dev.ekvedaras.laravelquery.utils

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.Query
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import org.jetbrains.annotations.NotNull

object ElementTypes {
    const val PhpArray = 1889
    val ArrayValues = listOf(805, 816)
}

class PsiUtils private constructor() {
    companion object {
        fun PsiElement.containsVariable(): Boolean = this.textContains('$')
        fun CompletionParameters.containsVariable(): Boolean = this.position.containsVariable()
        fun String.containsAlias(): Boolean = this.contains(" as ")
        fun PsiElement.isPhpArray(): Boolean = this.typeAsInt() == ElementTypes.PhpArray
        fun PsiElement.isArrayValue(): Boolean = ElementTypes.ArrayValues.contains(this.typeAsInt())
        fun String.unquoteAndCleanup() = this.replace("IntellijIdeaRulezzz", "").trim('\'', '"').trim()
        fun Variable.references(): @NotNull Query<PsiReference> =
            ReferencesSearch.search(this.originalElement)

        fun PsiReference.statementFirstPsiChild(): PsiElement? = this.element.parentOfType<Statement>()?.firstPsiChild
        private fun PsiElement.typeAsInt(): Int = this.elementType?.index?.toInt() ?: 0
    }
}
