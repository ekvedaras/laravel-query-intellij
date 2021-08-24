package dev.ekvedaras.laravelquery.reference

import com.intellij.database.psi.DbPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.models.DbReferenceExpression

class TableOrViewPsiReference(element: PsiElement, private val type: DbReferenceExpression.Companion.Type) :
    PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        val target = DbReferenceExpression(element, type)

        rangeInElement = if (target.schema.isNotEmpty() && target.ranges.size > 1) {
            target.ranges[1]
        } else {
            target.ranges.first()
        }

//       TODO uncomment when 2021.3 comes out
//        DbUtil.getDataSources(element.project).forEach { dataSource ->
//            val dbTable = dataSource.findElement(target.table.firstOrNull())
//            if (dbTable != null) {
//                return dbTable
//            }
//        }
//
//        return null

        return DbPsiFacade.getInstance(element.project).findElement(target.table.firstOrNull())
    }
}
