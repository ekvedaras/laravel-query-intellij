package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.model.DasTable
import com.intellij.database.psi.DbPsiFacade
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class TableOrViewPsiReference(element: PsiElement, private val table: DasTable, forColumn: Boolean = false) :
    PsiReferenceBase<PsiElement>(element, TextRange.allOf(element.text)) {
    init {
        rangeInElement = rangeInElement.shiftRight(1)

        if (element.text.contains(" as ")) {
            rangeInElement = rangeInElement.grown(element.text.indexOf(" as ") - element.textLength + 1)
        }

        // TODO transfer all these range calculations to DbReferenceExpression
        if (element.text.contains(".")) {
            if (forColumn) {
                if (element.text.split(".").size > 2) { // schema.table.name
                    val lengthSchema = element.text.substringBefore(".").length
                    val columnLength = element.text.substringAfterLast(".").length
                    rangeInElement = rangeInElement.shiftRight(lengthSchema).grown(-lengthSchema - columnLength)
                } else { // table.name or schema.table
                    var length = element.text.substringBefore(table.name).length
                    if (length == 1) { // table.name
                        length = element.text.substringAfter(".").length
                        rangeInElement = rangeInElement.grown(-length)
                    } else { // schema.table
                        rangeInElement = rangeInElement.shiftRight(length - 1).grown(-length + 1)
                    }
                }
            } else {
                val length = element.text.substringBefore(".").length
                rangeInElement = rangeInElement.shiftRight(length).grown(-length)
            }
        }

        rangeInElement = rangeInElement.grown(-2)
    }

    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(table)
    }
}