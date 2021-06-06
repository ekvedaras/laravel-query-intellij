package dev.ekvedaras.laravelquery.reference

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.rd.util.addUnique
import com.jetbrains.rd.util.lifetime.Lifetime
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.canHaveColumnsInArrayValues
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForColumns
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForForeignKeys
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForIndexes
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForKeys
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isBuilderMethodForUniqueIndexes
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isColumnIn
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isForIndexes
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isForKeys
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isForUniqueIndexes
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
    companion object {
        val isResolving = mutableListOf<PsiElement>()
    }

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        if (isResolving.contains(element)) {
            return PsiReference.EMPTY_ARRAY
        }

        val method = MethodUtils.resolveMethodReference(element) ?: return PsiReference.EMPTY_ARRAY
        val project = method.project

        if (shouldNotInspect(project, method, element)) {
            return PsiReference.EMPTY_ARRAY
        }

        isResolving.addUnique(Lifetime.Eternal, element)

        val target = DbReferenceExpression(
            element,
            when {
                method.isForIndexes() || method.isForUniqueIndexes() -> DbReferenceExpression.Companion.Type.Index
                method.isForKeys() -> DbReferenceExpression.Companion.Type.Key
                else -> DbReferenceExpression.Companion.Type.ForeignKey
            }
        )
        var references = arrayOf<PsiReference>()

        target.index.forEach { references += IndexPsiReference(target, it) }
        target.key.forEach { references += KeyPsiReference(target, it) }
        target.foreignKey.forEach { references += ForeignKeyPsiReference(target, it) }

        isResolving.remove(element)

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
