package dev.ekvedaras.laravelquery.reference

import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.nameWithoutPrefix

class KeyPsiReference(element: PsiElement) : PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        val target = DbReferenceExpression(element, DbReferenceExpression.Companion.Type.Key)
        val tables = target.tablesAndAliases.values.map { it.first }

        rangeInElement = target.ranges.last()

        DbUtil.getDataSources(element.project).filter {
            LaravelQuerySettings.getInstance(element.project).interestedIn(it)
        }.forEach { dataSource ->
            val dbKey = dataSource.findElement(target.key.find {
                tables.contains(it.table?.nameWithoutPrefix(element.project))
            })
            if (dbKey != null) {
                return dbKey
            }
        }

        return null
    }
}
