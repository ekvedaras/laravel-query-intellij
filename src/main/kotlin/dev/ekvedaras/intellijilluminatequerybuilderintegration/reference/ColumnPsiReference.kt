package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.model.DasColumn
import com.intellij.database.psi.DbPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression

class ColumnPsiReference(target: DbReferenceExpression, private val column: DasColumn) :
    PsiReferenceBase<PsiElement>(target.expression, target.ranges.last()) {
    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(column)
    }
}