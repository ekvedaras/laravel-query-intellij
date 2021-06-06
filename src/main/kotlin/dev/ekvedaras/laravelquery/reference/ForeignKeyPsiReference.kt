package dev.ekvedaras.laravelquery.reference

import com.intellij.database.model.DasForeignKey
import com.intellij.database.psi.DbPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.models.DbReferenceExpression

class ForeignKeyPsiReference(target: DbReferenceExpression, private val foreignKey: DasForeignKey) :
    PsiReferenceBase<PsiElement>(target.expression, target.ranges.last()) {
    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(foreignKey)
    }
}
