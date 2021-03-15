package dev.ekvedaras.laravelquery.utils

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.impl.ClassConstantReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.GroupStatementImpl
import com.jetbrains.php.lang.psi.elements.impl.ParameterListImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpReturnImpl
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRelationClosure
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup

class ModelRelationResolver(
    private val reference: DbReferenceExpression,
    private val tableAndAliasCollector: TableAndAliasCollector
) {
    fun resolveModelAndRelationTables(methods: MutableList<MethodReference>, method: MethodReference) {
        val modelReference: PhpTypedElement = tableAndAliasCollector.resolveModelReference(methods) ?: return
        val model = modelReference.getClass(reference.project) ?: return

        tableAndAliasCollector.resolveTableName(model)

        val deepParent =
            method.parent?.parent?.parent?.parent?.parent?.parent ?: return // TODO utilize parentOfType<>() ?

        if (deepParent.isInsideRelationClosure()) {
            resolveRelationTable(deepParent, model)
        }
    }

    @Suppress("ReturnCount")
    private fun resolveRelationTable(deepParent: PsiElement, model: PhpClass) {
        val relationName = deepParent.firstChild.text.unquoteAndCleanup()
        val relationMethod = model.methods.firstOrNull { it.name == relationName } ?: return
        if (relationMethod.lastChild !is GroupStatementImpl) {
            return
        }
        val returnStatement = MethodUtils.firstChildOfType(
            relationMethod.lastChild as GroupStatementImpl,
            PhpReturnImpl::class.java.name
        ) ?: return
        val firstParam = (
            MethodUtils.firstChildOfType(
                returnStatement,
                ParameterListImpl::class.java.name
            ) as? ParameterListImpl
            )?.getParameter(0) ?: return

        when (firstParam) {
            is ClassConstantReferenceImpl -> {
                tableAndAliasCollector.resolveTableName(
                    firstParam.classReference?.getClass(reference.project) ?: return
                )
            }
            is StringLiteralExpressionImpl -> {
                reference.tablesAndAliases[firstParam.contents] = firstParam.contents to null
            }
        }
    }
}
