package dev.ekvedaras.laravelquery.v4.reference

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForForeignKeys
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForIndexes
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForKeys
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForUniqueIndexes
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isForeignKeyIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isIndexIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsidePhpArrayOrValue
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRegularFunction
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInteresting
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isKeyIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isUniqueIndexIn
import dev.ekvedaras.laravelquery.utils.MethodUtils
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.containsVariable

class IndexReferenceProvider : PsiReferenceProvider() {

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY
        val project = method.project

        if (shouldNotInspect(project, method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        var references = arrayOf<PsiReference>()

        if (method.isBuilderMethodForIndexes()) {
            references += IndexPsiReference(element)
        } else if (method.isBuilderMethodForKeys() || method.isBuilderMethodForUniqueIndexes()) {
            references += KeyPsiReference(element)
        } else if (method.isBuilderMethodForForeignKeys()) {
            references += ForeignKeyPsiReference(element)
        }

        return references
    }

    private fun shouldNotInspect(project: Project, method: MethodReference, element: PsiElement) =
        !ApplicationManager.getApplication().isReadAccessAllowed ||
            element.containsVariable() ||
            (
                !method.isBuilderMethodForIndexes() &&
                    !method.isBuilderMethodForUniqueIndexes() &&
                    !method.isBuilderMethodForKeys() &&
                    !method.isBuilderMethodForForeignKeys()
                ) ||
            (
                !element.isIndexIn(method) &&
                    !element.isUniqueIndexIn(method) &&
                    !element.isKeyIn(method) &&
                    !element.isForeignKeyIn(method)
                ) ||
            element.isInsideRegularFunction() ||
            element.isInsidePhpArrayOrValue() ||
            !method.isInteresting(project)
}
