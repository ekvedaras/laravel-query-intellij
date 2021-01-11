package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.model.DasNamespace
import com.intellij.database.psi.DbPsiFacade
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class SchemaPsiReference(element: PsiElement, private val schema: DasNamespace) :
    PsiReferenceBase<PsiElement>(element, TextRange.allOf(element.text)) {
    init {
        rangeInElement = rangeInElement.shiftRight(1)

        if (element.text.contains(".")) {
            val length = element.text.substringAfter(".").length
            rangeInElement = rangeInElement.grown(-length)
        }

        rangeInElement = rangeInElement.grown(-2)
    }

    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(schema)
    }
}