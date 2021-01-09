package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.model.DasTable
import com.intellij.database.psi.DbPsiFacade
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class TableOrViewPsiReference(element: PsiElement, private val table: DasTable) :
    PsiReferenceBase<PsiElement>(element, TextRange.allOf(element.text)) {
    init {
        rangeInElement = rangeInElement.shiftRight(1)

        if (element.text.contains(" as ")) {
            rangeInElement = rangeInElement.grown(element.text.indexOf(" as ") - element.textLength + 1)
        }

        rangeInElement = rangeInElement.grown(-2)
    }

    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(table)
    }
}