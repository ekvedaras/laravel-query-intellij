package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.model.DasColumn
import com.intellij.database.psi.DbPsiFacade
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class ColumnPsiReference(element: PsiElement, private val column: DasColumn) :
    PsiReferenceBase<PsiElement>(element, TextRange.allOf(element.text)) {
    init {
        rangeInElement = rangeInElement.shiftRight(1)

        if (element.text.contains(" as ")) {
            rangeInElement = rangeInElement.grown(element.text.indexOf(" as ") - element.textLength + 1)

            if (element.text.contains(".")) {
                val length = element.text.substringAfterLast(".").substringBefore(" as").length
                rangeInElement = rangeInElement.shiftRight(length).grown(-length)
            }
        } else {
            if (element.text.contains(".")) {
                val length = element.text.substringBeforeLast(".").length
                rangeInElement = rangeInElement.shiftRight(length).grown(-length)
            }
        }

        rangeInElement = rangeInElement.grown(-2)
    }

    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(column)
    }
}