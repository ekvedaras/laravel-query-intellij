package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.PsiUtils.Companion.isPhpArray

object ElementTypes {
    const val PhpArray = 1889
    const val ArrayValue = 805
}

class PsiUtils {
    companion object {
        fun PsiElement.containsVariable(): Boolean = this.textContains('$')
        fun CompletionParameters.containsVariable(): Boolean = this.position.containsVariable()
        fun PsiElement.typeAsInt(): Int = this.elementType?.index?.toInt() ?: 0
        fun PsiElement.isPhpArray(): Boolean = this.typeAsInt() == ElementTypes.PhpArray
        fun PsiElement.isArrayValue(): Boolean = this.typeAsInt() == ElementTypes.ArrayValue
    }
}