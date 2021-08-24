package dev.ekvedaras.laravelquery.reference

import com.intellij.database.psi.DbPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.models.DbReferenceExpression

class SchemaPsiReference(element: PsiElement, private val type: DbReferenceExpression.Companion.Type) :
    PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        val target = DbReferenceExpression(element, type)

        rangeInElement = target.ranges.first()

//       TODO uncomment when 2021.3 comes out
//        DbUtil.getDataSources(element.project).forEach { dataSource ->
//            val dbSchema = dataSource.findElement(target.schema.firstOrNull())
//            if (dbSchema != null) {
//                return dbSchema
//            }
//        }
//
//        return null

        return DbPsiFacade.getInstance(element.project).findElement(target.schema.firstOrNull())
    }
}
