package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.model.DasTable
import com.intellij.database.psi.DbPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression

class TableOrViewPsiReference(target: DbReferenceExpression, private val table: DasTable, forColumn: Boolean = false) :
    PsiReferenceBase<PsiElement>(
        target.expression,
        if (target.schema.isNotEmpty()) target.ranges[1] else target.ranges.first()
    ) {
    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(table)
    }
}