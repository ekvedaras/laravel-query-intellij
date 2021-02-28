package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.*
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.ClassUtils.Companion.isChildOf
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.tables
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.isInsideRelationClosure
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils.Companion.tableName
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils.Companion.getClass
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils.Companion.isJoinOrRelation
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils.Companion.referencesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils.Companion.statementFirstPsiChild
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils.Companion.unquote
import java.util.*

class TableAndAliasCollector(private val reference: DbReferenceExpression) {
    fun collect() {
        val method = MethodUtils.resolveMethodReference(reference.expression) ?: return
        val methods = Collections.synchronizedList(mutableListOf<MethodReference>())

        collectMethodsAcrossVariableReferences(methods, method)
        collectMethodsInCurrentTree(methods, method)

        resolveModelAndRelationTables(methods, method)

        methods
            .filter { LaravelUtils.BuilderTableMethods.contains(it.name) }
            .forEach { scanMethodReference(it) }
    }

    private fun collectMethodsAcrossVariableReferences(methods: MutableList<MethodReference>, method: MethodReference) {
        val variable = method.parentOfType<Statement>()!!.firstPsiChild?.firstPsiChild
        if (variable !is VariableImpl) return

        variable.referencesInParallel().forEach loop@{ variableReference ->
            val element = variableReference.statementFirstPsiChild() ?: return@loop

            // $var = query()->table();
            if (element is AssignmentExpressionImpl && element.lastChild is MethodReference) {
                methods.addAll(MethodUtils.findMethodsInTree(element.lastChild))
                return@loop
            }

            // $var->where()
            if (element is MethodReference) {
                methods.addAll(
                    MethodUtils.findMethodsInTree(
                        // $var->where(['relation' => function (Relation $relation) { $relation->where() }])
                        // $var->join('table', function (JoinClause $join) { $join->on() })
                        // TODO: this could go deep. Make it walk up the tree dynamically? Or 1 level up is enough?
                        if (element.isJoinOrRelation(reference.project))
                            element.parent.parentOfType<Statement>()!!.parentOfType<Statement>()!!
                        else
                            element.parent
                    )
                )
            }
        }
    }

    private fun collectMethodsInCurrentTree(methods: MutableList<MethodReference>, method: MethodReference) {
        if (methods.isNotEmpty()) return

        methods.addAll(
            MethodUtils.findMethodsInTree(
                // $var->where(['relation' => function (Relation $relation) { $relation->where() }])
                // $var->join('table', function (JoinClause $join) { $join->on() })
                // TODO: this could go deep. Make it walk up the tree dynamically? Or 1 level up is enough?
                if (method.isJoinOrRelation(reference.project))
                    method.parentOfType<Statement>()!!
                        .parentOfType<Statement>()!!
                        .parentOfType<Statement>()!!
                        .firstChild
                else
                    method.parentOfType<Statement>()!!.firstChild
            )
        )
    }

    private fun resolveModelAndRelationTables(methods: MutableList<MethodReference>, method: MethodReference) {
        val modelReference: PhpTypedElement = resolveModelReference(methods) ?: return
        val model = modelReference.getClass(reference.project)

        resolveTableName(model)

        val deepParent =
            method.parent?.parent?.parent?.parent?.parent?.parent ?: return // TODO utilize parentOfType<>() ?

        if (deepParent.isInsideRelationClosure()) {
            resolveRelationTable(deepParent, model)
        }
    }

    private fun resolveRelationTable(deepParent: PsiElement, model: PhpClass) {
        val relationName = deepParent.firstChild.text.unquote()
        val relationMethod = model.methods.firstOrNull { it.name == relationName } ?: return
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
                resolveTableName(
                    firstParam.classReference?.getClass(reference.project) ?: return
                )
            }
            is StringLiteralExpressionImpl -> {
                reference.tablesAndAliases[firstParam.contents] = firstParam.contents to null
            }
        }
    }

    private fun resolveTableName(model: PhpClass) {
        val name = model.tableName()
        reference.tablesAndAliases[name] = name to null
    }

    private fun resolveModelReference(methods: MutableList<MethodReference>): PhpTypedElement? {
        var modelReference: PhpTypedElement? = null

        if (methods.none { it.name == "from" }) {
            modelReference = methods.find { methodReference ->
                (
                        methodReference.firstChild is ClassReferenceImpl && (
                                PhpIndex.getInstance(reference.project)
                                    .getClassesByFQN(
                                        (methodReference.firstChild as ClassReferenceImpl).declaredType.types.first()
                                    )
                                    .first() as PhpClassImpl
                                )
                            .isChildOf(LaravelUtils.modelClass(reference.project))
                        ) ||
                        (
                                methodReference.firstChild is VariableImpl && (
                                        PhpIndex.getInstance(reference.project)
                                            .getClassesByFQN(
                                                (methodReference.firstChild as VariableImpl).declaredType.types.first()
                                            )
                                            .firstOrNull() as? PhpClassImpl
                                        )
                                    ?.isChildOf(LaravelUtils.modelClass(reference.project)) == true
                                )
            }?.firstChild as? PhpTypedElement

            if (modelReference == null) {
                modelReference = methods.find { methodReference ->
                    methodReference.firstChild is ParenthesizedExpressionImpl &&
                            (
                                    PhpIndex.getInstance(reference.project)
                                        .getClassesByFQN(
                                            (methodReference.firstChild?.firstChild?.nextSibling?.firstChild?.nextSibling?.nextSibling as? ClassReferenceImpl)?.declaredType?.types?.first()
                                        )
                                        .first() as PhpClassImpl
                                    )
                                .isChildOf("\\Illuminate\\Database\\Eloquent\\Model")
                }?.firstChild?.firstChild?.nextSibling?.firstChild?.nextSibling?.nextSibling as? PhpTypedElement
            }
        }
        return modelReference
    }

    private fun scanMethodReference(method: MethodReference) {
        if (method.getParameter(0) !is StringLiteralExpressionImpl) {
            return
        }

        val definition = (method.getParameter(0) as StringLiteralExpressionImpl).contents.trim()

        var referencedTable: String = definition
        var referencedSchema: String? = null

        if (definition.contains(".")) {
            for (part in definition.split(".").reversed()) {
                if (referencedTable == definition) {
                    referencedTable = part.replace("IntellijIdeaRulezzz", "").trim()
                } else {
                    referencedSchema = part.replace("IntellijIdeaRulezzz", "").trim()
                }
            }
        }

        if (referencedTable.contains(" as ")) {
            val alias = referencedTable.substringAfter("as").trim()
            val table = referencedTable.substringBefore("as").trim()

            if (referencedSchema == null) {
                reference.project.dbDataSourcesInParallel().forEach { dataSource ->
                    val dasTable =
                        dataSource.tables().firstOrNull { dasTable -> dasTable.name == table }
                    if (dasTable != null) {
                        referencedSchema = dasTable.dasParent?.name
                    }
                }
            }

            reference.tablesAndAliases[alias] = table to referencedSchema
            reference.aliases[table] = alias to method.getParameter(0)!!
            return
        }

        if (referencedSchema == null) {
            reference.project.dbDataSourcesInParallel().forEach { dataSource ->
                val dasTable =
                    dataSource.tables().firstOrNull { dasTable -> dasTable.name == referencedTable }
                if (dasTable != null) {
                    referencedSchema = dasTable.dasParent?.name
                }
            }
        }

        if (!LaravelUtils.BuilderTableAliasParams.containsKey(method.name)) {
            reference.tablesAndAliases[referencedTable] = referencedTable to referencedSchema
            return
        }

        val aliasParam: Int = LaravelUtils.BuilderTableAliasParams[method.name] ?: return
        val alias: String? = (method.getParameter(aliasParam) as? StringLiteralExpressionImpl)?.contents

        reference.tablesAndAliases[alias ?: referencedTable] = referencedTable to referencedSchema

        if (alias != null && method.getParameter(aliasParam) != null) {
            reference.aliases[referencedTable] = alias to method.getParameter(aliasParam)!!
        }
    }
}