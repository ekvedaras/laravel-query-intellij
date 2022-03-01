package dev.ekvedaras.laravelquery.reference

import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings

class TableOrViewPsiReference(element: PsiElement, private val type: DbReferenceExpression.Companion.Type) :
    PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        val target = DbReferenceExpression(element, type)

        rangeInElement = if (target.schema.isNotEmpty() && target.ranges.size > 1) {
            target.ranges[1]
        } else {
            target.ranges.first()
        }

        DbUtil.getDataSources(element.project).filter {
            LaravelQuerySettings.getInstance(element.project).interestedIn(it)
        }.forEach { dataSource ->
            val dbTable = dataSource.findElement(target.table.firstOrNull())
            if (dbTable != null) {
                return dbTable
            }
        }

        return null
    }
}
