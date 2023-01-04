package dev.ekvedaras.laravelquery.v4.reference

import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.v4.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import dev.ekvedaras.laravelquery.v4.utils.DatabaseUtils.Companion.nameWithoutPrefix

class IndexPsiReference(element: PsiElement) : PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        val target = DbReferenceExpression(element, DbReferenceExpression.Companion.Type.Index)
        val tables = target.tablesAndAliases.values.map { it.first }

        rangeInElement = target.ranges.last()

        DbUtil.getDataSources(element.project).filter {
            LaravelQuerySettings.getInstance(element.project).interestedIn(it)
        }.forEach { dataSource ->
            val dbIndex = dataSource.findElement(target.index.find {
                tables.contains(it.table?.nameWithoutPrefix(element.project))
            })
            if (dbIndex != null) {
                return dbIndex
            }
        }

        return null
    }
}
