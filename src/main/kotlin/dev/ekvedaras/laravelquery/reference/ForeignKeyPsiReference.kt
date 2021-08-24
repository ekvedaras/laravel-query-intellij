package dev.ekvedaras.laravelquery.reference

import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.laravelquery.models.DbReferenceExpression

class ForeignKeyPsiReference(element: PsiElement) : PsiReferenceBase<PsiElement>(element) {
    override fun resolve(): PsiElement? {
        val target = DbReferenceExpression(element, DbReferenceExpression.Companion.Type.ForeignKey)
        val tables = target.tablesAndAliases.values.map { it.first }

        rangeInElement = target.ranges.last()

        DbUtil.getDataSources(element.project).forEach { dataSource ->
            val dbForeignKey = dataSource.findElement(target.foreignKey.find { tables.contains(it.table?.name) })
            if (dbForeignKey != null) {
                return dbForeignKey
            }
        }

        return null
    }
}
