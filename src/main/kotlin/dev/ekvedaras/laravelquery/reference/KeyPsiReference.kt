package dev.ekvedaras.laravelquery.reference

import com.intellij.database.model.DasTableKey
import com.intellij.database.psi.DbPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.models.DbReferenceExpression

class KeyPsiReference(target: DbReferenceExpression, private val key: DasTableKey) :
    PsiReferenceBase<PsiElement>(target.expression, target.ranges.last()) {
    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(key)
    }
}
