package dev.ekvedaras.laravelquery.reference

import com.intellij.database.model.DasIndex
import com.intellij.database.psi.DbPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.models.DbReferenceExpression

class IndexPsiReference(target: DbReferenceExpression, private val index: DasIndex) :
    PsiReferenceBase<PsiElement>(target.expression, target.ranges.last()) {
    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(index)
    }
}
