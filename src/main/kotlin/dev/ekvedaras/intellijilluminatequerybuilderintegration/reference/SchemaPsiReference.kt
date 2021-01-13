package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.model.DasNamespace
import com.intellij.database.psi.DbPsiFacade
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression

class SchemaPsiReference(target: DbReferenceExpression, private val schema: DasNamespace) :
    PsiReferenceBase<PsiElement>(target.expression, target.ranges.first()) {
    override fun resolve(): PsiElement? {
        return DbPsiFacade.getInstance(element.project).findElement(schema)
    }
}