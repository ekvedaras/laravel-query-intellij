package dev.ekvedaras.intellijilluminatequerybuilderintegration.models

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.*
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.ClassUtils.Companion.asTableName
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.ClassUtils.Companion.isChildOf
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.tables
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DbReferenceResolver
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils
import java.util.*

class DbReferenceExpression(val expression: PsiElement, val type: Type) {
    companion object {
        enum class Type {
            Table,
            Column
        }
    }

    val project = expression.project

    val tablesAndAliases = mutableMapOf<String, Pair<String, String?>>()
    val aliases = mutableMapOf<String, Pair<String, PsiElement>>()

    var schema = mutableListOf<DasNamespace>()
    var table = mutableListOf<DasTable>()
    var column = mutableListOf<DasColumn>()
    var alias: String? = null

    val parts = mutableListOf<String>()
    val ranges = mutableListOf<TextRange>()

    init {
        parts.addAll(
            expression.text
                .trim('"')
                .trim('\'')
                .split(".")
                .map { it.replace("IntellijIdeaRulezzz", "").substringBefore(" as").trim() }
        )

        for (part in parts) {
            ranges.add(TextRange.from(if (ranges.isNotEmpty()) ranges.last().endOffset + 1 else 1, part.length))
        }

        if (type == Type.Column) collectTablesAndAliases()

        DbReferenceResolver(this).resolve()
    }

    private fun collectTablesAndAliases() {
        val method = MethodUtils.resolveMethodReference(expression) ?: return
        val project = expression.project
        val methods = mutableListOf<MethodReference>()

        if (method.parentOfType<Statement>()!!.firstPsiChild?.firstPsiChild is VariableImpl) {
            // Resolve all statements for the variable

            ReferencesSearch.search(method.parentOfType<Statement>()!!.firstPsiChild!!.firstPsiChild!!.originalElement)
                .findAll()
                .forEach loop@{ reference ->
                    val tree = reference.element.parentOfType<Statement>()?.firstPsiChild ?: return@loop

                    if (tree is AssignmentExpressionImpl && tree.lastChild is MethodReference) {
                        methods.addAll(
                            MethodUtils.findMethodsInTree(
                                tree.lastChild
                            )
                        )
                    } else if (tree is MethodReference) {
                        methods.addAll(
                            MethodUtils.findMethodsInTree(
                                if (MethodUtils.resolveMethodClasses(tree, project).any {
                                        it.fqn == "\\Illuminate\\Database\\Query\\JoinClause" ||
                                                it.fqn == "\\Illuminate\\Database\\Eloquent\\Relations\\Relation"
                                    }
                                )
                                    tree.parent.parentOfType<Statement>()!!.parentOfType<Statement>()!!
                                else
                                    tree.parent
                            )
                        )
                    }
                }
        }

        if (methods.isEmpty()) {
            methods.addAll(
                MethodUtils.findMethodsInTree(
                    if (MethodUtils.resolveMethodClasses(method, project).any {
                            it.fqn == "\\Illuminate\\Database\\Query\\JoinClause" ||
                                    it.fqn == "\\Illuminate\\Database\\Eloquent\\Relations\\Relation"
                        }
                    )
                        method.parentOfType<Statement>()!!.parentOfType<Statement>()!!
                            .parentOfType<Statement>()!!.firstChild
                    else
                        method.parentOfType<Statement>()!!.firstChild
                )
            )
        }

        // <editor-fold desc="Resolve model and table from static call like User::query()">
        var modelReference: PhpTypedElement? = null

        if (methods.none { it.name == "from" }) {
            modelReference = methods.find {
                (
                        it.firstChild is ClassReferenceImpl && (
                                PhpIndex.getInstance(project)
                                    .getClassesByFQN(
                                        (it.firstChild as ClassReferenceImpl).declaredType.types.first()
                                    )
                                    .first() as PhpClassImpl
                                )
                            .isChildOf(
                                PhpIndex.getInstance(project)
                                    .getClassesByFQN("\\Illuminate\\Database\\Eloquent\\Model")
                                    .first()
                            )
                        ) ||
                        (
                                it.firstChild is VariableImpl && (
                                        PhpIndex.getInstance(project)
                                            .getClassesByFQN(
                                                (it.firstChild as VariableImpl).declaredType.types.first()
                                            )
                                            .firstOrNull() as? PhpClassImpl
                                        )
                                    ?.isChildOf(
                                        PhpIndex.getInstance(project)
                                            .getClassesByFQN("\\Illuminate\\Database\\Eloquent\\Model")
                                            .first()
                                    ) == true
                                )
            }?.firstChild as? PhpTypedElement

            if (modelReference == null) {
                modelReference = methods.find {
                    it.firstChild is ParenthesizedExpressionImpl &&
                            (
                                    PhpIndex.getInstance(project)
                                        .getClassesByFQN(
                                            (it.firstChild?.firstChild?.nextSibling?.firstChild?.nextSibling?.nextSibling as? ClassReferenceImpl)?.declaredType?.types?.first()
                                        )
                                        .first() as PhpClassImpl
                                    )
                                .isChildOf("\\Illuminate\\Database\\Eloquent\\Model")
                }?.firstChild?.firstChild?.nextSibling?.firstChild?.nextSibling?.nextSibling as? PhpTypedElement
            }
        }

        if (modelReference != null) {
            val model = PhpIndex.getInstance(project)
                .getClassesByFQN(modelReference.declaredType.types.first())
                .first()

            val tableName = model.fields.find { it.name == "table" }
            if (tableName != null && tableName.defaultValue != null) {
                val name = tableName.defaultValue!!.text.trim('\'').trim('"')
                tablesAndAliases[name] = name to null
            } else {
                val name = model.asTableName()
                tablesAndAliases[name] = name to null
            }

            val deepParent = method.parent?.parent?.parent?.parent?.parent?.parent
            if (deepParent is ArrayHashElementImpl && deepParent.parentOfType<MethodReferenceImpl>()?.name == "with") {
                val relationName = deepParent.firstChild.text.replace("'", "").replace("\"", "")
                val relationMethod = model.methods.firstOrNull { it.name == relationName }

                if (relationMethod != null) {
                    val returnStatement = MethodUtils.firstChildOfType(
                        (relationMethod as MethodImpl).lastChild as GroupStatementImpl,
                        PhpReturnImpl::class.java.name
                    )

                    if (returnStatement != null) {
                        val firstParam = (
                                MethodUtils.firstChildOfType(
                                    returnStatement,
                                    ParameterListImpl::class.java.name
                                ) as? ParameterListImpl
                                )?.getParameter(0)

                        if (firstParam != null) {
                            if (firstParam is ClassConstantReferenceImpl) {
                                val relationModel = PhpIndex.getInstance(project)
                                    .getClassesByFQN(firstParam.classReference?.declaredType?.types?.first())
                                    .first()

                                val relationTableName = relationModel.fields.find { it.name == "table" }
                                if (relationTableName != null && relationTableName.defaultValue != null) {
                                    val name = relationTableName.defaultValue!!.text.trim('\'').trim('"')
                                    tablesAndAliases[name] = name to null
                                } else {
                                    val name = relationModel.asTableName()
                                    tablesAndAliases[name] = name to null
                                }
                            } else if (firstParam is StringLiteralExpressionImpl) {
                                tablesAndAliases[firstParam.contents] = firstParam.contents to null
                            }
                        }
                    }
                }
            }
        }
        // </editor-fold>

        methods
            .filter { LaravelUtils.BuilderTableMethods.contains(it.name) }
            .forEach loop@{
                if (it.getParameter(0) !is StringLiteralExpressionImpl) {
                    return@loop
                }

                val definition = (it.getParameter(0) as StringLiteralExpressionImpl).contents.trim()

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
                        project.dbDataSourcesInParallel().forEach { dataSource ->
                            val dasTable =
                                dataSource.tables().firstOrNull { dasTable -> dasTable.name == table }
                            if (dasTable != null) {
                                referencedSchema = dasTable.dasParent?.name
                            }
                        }
                    }

                    tablesAndAliases[alias] = table to referencedSchema
                    aliases[table] = alias to it.getParameter(0)!!
                    return@loop
                }

                if (referencedSchema == null) {
                    project.dbDataSourcesInParallel().forEach { dataSource ->
                        val dasTable =
                            dataSource.tables().firstOrNull { dasTable -> dasTable.name == referencedTable }
                        if (dasTable != null) {
                            referencedSchema = dasTable.dasParent?.name
                        }
                    }
                }

                if (!LaravelUtils.BuilderTableAliasParams.containsKey(it.name)) {
                    tablesAndAliases[referencedTable] = referencedTable to referencedSchema
                    return@loop
                }

                val aliasParam: Int = LaravelUtils.BuilderTableAliasParams[it.name] ?: return@loop
                val alias: String? = (it.getParameter(aliasParam) as? StringLiteralExpressionImpl)?.contents

                tablesAndAliases[alias ?: referencedTable] = referencedTable to referencedSchema

                if (alias != null && it.getParameter(aliasParam) != null) {
                    aliases[referencedTable] = alias to it.getParameter(aliasParam)!!
                }
            }
    }
}
