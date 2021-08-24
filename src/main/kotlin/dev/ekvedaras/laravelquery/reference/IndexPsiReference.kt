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

//       TODO uncomment when 2021.3 comes out
//        DbUtil.getDataSources(element.project).forEach { dataSource ->
//            val dbIndex = dataSource.findElement(target.index.find { tables.contains(it.table?.name) })
//            if (dbIndex != null) {
//                return dbIndex
//            }
//        }
//
//        return null

        return DbPsiFacade.getInstance(element.project).findElement(
            target.index.find { tables.contains(it.table?.name) }
        )
    }
}
