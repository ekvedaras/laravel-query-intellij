package dev.ekvedaras.laravelquery.reference

import com.intellij.database.psi.DbPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.models.DbReferenceExpression

class IndexPsiReference(element: PsiElement) : PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        val target = DbReferenceExpression(element, DbReferenceExpression.Companion.Type.Index)
        val tables = target.tablesAndAliases.values.map { it.first }

        rangeInElement = target.ranges.last()

        return DbPsiFacade.getInstance(element.project).findElement(
            target.index.find { tables.contains(it.table?.name) }
        )
    }
}
