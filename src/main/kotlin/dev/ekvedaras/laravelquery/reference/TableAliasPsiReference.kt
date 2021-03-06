package dev.ekvedaras.laravelquery.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class TableAliasPsiReference(element: PsiElement, range: TextRange, val target: PsiElement) :
    PsiReferenceBase<PsiElement>(element, range) {
    override fun resolve(): PsiElement {
        return target
    }
}
