package dev.ekvedaras.laravelquery.v4.utils

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
import dev.ekvedaras.laravelquery.utils.ClassUtils.Companion.isChildOf
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.isInsideRelationClosure
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup

class ModelRelationResolver(
    private val reference: DbReferenceExpression,
    private val tableAndAliasCollector: TableAndAliasCollector
) {
    fun resolveModelAndRelationTables(methods: MutableList<MethodReference>, method: MethodReference) {
        val modelReference: PhpTypedElement = tableAndAliasCollector.resolveModelReference(methods) ?: return
        val model = modelReference.getClass(reference.project) ?: return

        // $user->customer()->create(['']). Detect customer() and resolve table name as customers table only
        val relationMethod = methods.lastOrNull { mm ->
            MethodUtils.resolveMethodTypeClasses(mm, method.project).any {
                it.isChildOf(LaravelClasses.Relation)
            }
        }
        if (relationMethod != null && relationMethod.name != null) {
            resolveRelationTable(relationMethod.name ?: "", model)
            return
        }

        tableAndAliasCollector.resolveTableName(model)

        val deepParent =
            method.parent?.parent?.parent?.parent?.parent?.parent // TODO utilize parentOfType<>() ?
        if (deepParent?.isInsideRelationClosure() == true) {
            resolveRelationTable(deepParent, model)
        }
    }

    @Suppress("ReturnCount")
    private fun resolveRelationTable(deepParent: PsiElement, model: PhpClass) {
        resolveRelationTable(
            deepParent.firstChild.text.unquoteAndCleanup(),
            model
        )
    }

    @Suppress("ReturnCount")
    private fun resolveRelationTable(relationName: String, model: PhpClass) {
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
