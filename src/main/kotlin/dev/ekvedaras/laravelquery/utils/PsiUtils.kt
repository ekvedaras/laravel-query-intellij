package dev.ekvedaras.laravelquery.utils

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfType
import com.intellij.util.Query
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import org.jetbrains.annotations.NotNull

@Suppress("MagicNumber")
object ElementTypes {
    val PhpArray = listOf(1386, 1889)
    const val ArrayValues = "Array value"
}

class PsiUtils private constructor() {
    companion object {
        fun PsiElement.containsVariable(): Boolean = this.textContains('$')
        fun CompletionParameters.containsVariable(): Boolean = this.position.containsVariable()
        fun String.containsAlias(): Boolean = this.contains(" as ")
        fun PsiElement.isPhpArray(): Boolean = ElementTypes.PhpArray.contains(this.typeAsInt())
        fun PsiElement.isArrayValue(): Boolean = this.elementType.toString() === ElementTypes.ArrayValues
        fun String.unquoteAndCleanup() = this.replace("IntellijIdeaRulezzz", "").trim('\'', '"').trim()
        fun Variable.references(): @NotNull Query<PsiReference> =
            ReferencesSearch.search(this.originalElement, ProjectScope.getProjectScope(this.project), false)

        fun PsiReference.statementFirstPsiChild(): PsiElement? = this.element.parentOfType<Statement>()?.firstPsiChild
        private fun PsiElement.typeAsInt(): Int = this.elementType?.index?.toInt() ?: 0
    }
}
