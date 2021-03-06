package dev.ekvedaras.laravelquery.reference

import com.intellij.database.model.DasTable
import com.intellij.database.psi.DbPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.models.DbReferenceExpression

class TableOrViewPsiReference(target: DbReferenceExpression, private val table: DasTable) :
    PsiReferenceBase<PsiElement>(
        target.expression,
        if (target.schema.isNotEmpty()) target.ranges[1] else target.ranges.first()
    ) {
    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(table)
    }
}
