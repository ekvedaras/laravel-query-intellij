package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.sql.psi.SqlTableDefinition
import com.intellij.sql.slicer.toSqlElement
import com.intellij.util.ProcessingContext

class TableOrViewReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val references: HashSet<PsiReference> = HashSet()

        DbUtil.getDataSources(element.project).forEach { dataSource ->
            DasUtil.getTables(dataSource.dataSource)
                .forEach {

                    val reference = it.toSqlElement<SqlTableDefinition>()?.reference
                    if (!it.isSystem && it.name == element.text && reference is PsiReference) {
                        references.add(reference)
                    }
                }
        }

        return references.toArray() as Array<PsiReference>
    }
}