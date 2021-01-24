package dev.ekvedaras.intellijilluminatequerybuilderintegration.models

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.*
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.ClassUtils.Companion.asTableName
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.ClassUtils.Companion.isChildOf
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class DbReferenceExpression(val expression: PsiElement, private val type: Type) {
    companion object {
        enum class Type {
            Table,
            Column
        }
    }

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
        findExpressionReferences()
    }

    private fun collectTablesAndAliases() {
        val method = MethodUtils.resolveMethodReference(expression) ?: return
        val methods = MethodUtils.findMethodsInTree(method.parentOfType<Statement>()!!.firstChild)

        //<editor-fold desc="Resolve model and table from static call like User::query()">
        var modelReference = methods.find {
            (it.firstChild is ClassReferenceImpl && (PhpIndex.getInstance(method.project)
                .getClassesByFQN(
                    (it.firstChild as ClassReferenceImpl).declaredType.types.first()
                )
                .first() as PhpClassImpl)
                .isChildOf(
                    PhpIndex.getInstance(method.project)
                        .getClassesByFQN("\\Illuminate\\Database\\Eloquent\\Model")
                        .first()
                ))
                    || (it.firstChild is VariableImpl && (PhpIndex.getInstance(method.project)
                .getClassesByFQN(
                    (it.firstChild as VariableImpl).declaredType.types.first()
                )
                .firstOrNull() as? PhpClassImpl)
                ?.isChildOf(
                    PhpIndex.getInstance(method.project)
                        .getClassesByFQN("\\Illuminate\\Database\\Eloquent\\Model")
                        .first()
                ) == true)
        }?.firstChild as? PhpTypedElement

        if (modelReference == null) {
            modelReference = methods.find {
                it.firstChild is ParenthesizedExpressionImpl &&
                        (PhpIndex.getInstance(method.project)
                            .getClassesByFQN(
                                (it.firstChild?.firstChild?.nextSibling?.firstChild?.nextSibling?.nextSibling as? ClassReferenceImpl)?.declaredType?.types?.first()
                            )
                            .first() as PhpClassImpl)
                            .isChildOf("\\Illuminate\\Database\\Eloquent\\Model")
            }?.firstChild?.firstChild?.nextSibling?.firstChild?.nextSibling?.nextSibling as? PhpTypedElement
        }

        if (modelReference != null) {
            val model = PhpIndex.getInstance(method.project)
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
        }
        //</editor-fold>

        methods
            .filter { LaravelUtils.BuilderTableMethods.contains(it.name) }
            .forEach loop@{
                if (it.getParameter(0) !is StringLiteralExpressionImpl) {
                    return@loop
                }

                val definition = (it.getParameter(0) as StringLiteralExpressionImpl).contents.trim()

                var _table: String = definition
                var _schema: String? = null

                if (definition.contains(".")) {
                    for (part in definition.split(".").reversed()) {
                        if (_table == definition) {
                            _table = part.replace("IntellijIdeaRulezzz", "").trim()
                        } else {
                            _schema = part.replace("IntellijIdeaRulezzz", "").trim()
                        }
                    }
                }

                if (_table.contains(" as ")) {
                    val alias = _table.substringAfter("as").trim()
                    val table = _table.substringBefore("as").trim()
                    tablesAndAliases[alias] = table to _schema
                    aliases[table] = alias to it.getParameter(0)!!
                    return@loop
                }

                if (!LaravelUtils.BuilderTableAliasParams.containsKey(it.name)) {
                    tablesAndAliases[_table] = _table to _schema
                    return@loop
                }

                val aliasParam: Int = LaravelUtils.BuilderTableAliasParams[it.name] ?: return@loop
                val alias: String? = (it.getParameter(aliasParam) as? StringLiteralExpressionImpl)?.contents

                tablesAndAliases[alias ?: _table] = _table to _schema

                if (alias != null && it.getParameter(aliasParam) != null) {
                    aliases[_table] = alias to it.getParameter(aliasParam)!!
                }
            }
    }

    private fun findExpressionReferences() {
        /**
         * For table
         */
        if (type == Type.Table) {
            // 1. 'schema' or 'schema.table'
            DbUtil.getDataSources(expression.project).forEach { dataSource ->
                schema.addAll(
                    DasUtil.getSchemas(dataSource)
                        .filter { it.name == parts.first() }
                        .toMutableList()
                )
            }

            if (parts.size == 1) {
                // 2. 'table'

                DbUtil.getDataSources(expression.project).forEach { dataSource ->
                    DasUtil.getTables(dataSource).forEach {
                        if (it.name == parts.last()) {
                            table.add(it)
                        } else if (tablesAndAliases[parts.last()]?.first == it.name) {
                            table.add(it)
                            alias = it.name
                        }
                    }
                }
            } else if (parts.size == 2) {
                // 3. 'schema.table'

                DbUtil.getDataSources(expression.project).forEach { dataSource ->
                    DasUtil.getSchemas(dataSource).filter { schema.contains(it) }.forEach { namespace ->
                        table.addAll(
                            DasUtil.getTables(dataSource)
                                .filter { it.dasParent?.name == namespace.name }
                                .filter { it.name == parts.last() }
                        )
                    }
                }
            }
        } else if (type == Type.Column) {
            /**
             * For column
             */
            if (parts.size == 1) {
                // 1. 'column'
                // 2. 'table'
                // 3. 'schema'
                // 4. 'alias'
                DbUtil.getDataSources(expression.project).forEach { dataSource ->
                    schema.addAll(
                        DasUtil.getSchemas(dataSource).filter { it.name == parts.first() }
                    )

                    DasUtil.getTables(dataSource).forEach { dasTable ->
                        if (dasTable.name == parts.first()) {
                            table.add(dasTable)
                        } else if (tablesAndAliases[parts.first()]?.first == dasTable.name) {
                            table.add(dasTable)
                            alias = dasTable.name
                        }

                        column.addAll(
                            DasUtil.getColumns(dasTable).filter { it.name == parts.first() }
                        )
                    }
                }

                // 4. 'alias'

            } else if (parts.size == 2) {
                // 5. 'table.column'
                // 6. 'schema.table'
                // 7. 'alias.column'
                DbUtil.getDataSources(expression.project).forEach { dataSource ->
                    schema.addAll(
                        DasUtil.getSchemas(dataSource).filter { it.name == parts.first() }
                    )

                    DasUtil.getTables(dataSource).forEach { dasTable ->
                        if (schema.isEmpty() || schema.contains(dasTable.dasParent)) {
                            if (dasTable.name == parts.first() || dasTable.name == parts.last()) {
                                table.add(dasTable)

                                column.addAll(
                                    DasUtil.getColumns(dasTable).filter { it.name == parts.last() }
                                )
                            } else if (schema.isEmpty() && (tablesAndAliases[parts.first()]?.first == dasTable.name || tablesAndAliases[parts.last()]?.first == dasTable.name)) {
                                table.add(dasTable)
                                alias = dasTable.name

                                column.addAll(
                                    DasUtil.getColumns(dasTable).filter { it.name == parts.last() }
                                )
                            }
                        }
                    }
                }
            } else if (parts.size == 3) {
                // 8. 'schema.table.column
                DbUtil.getDataSources(expression.project).forEach { dataSource ->
                    schema.addAll(
                        DasUtil.getSchemas(dataSource).filter { it.name == parts.first() }
                    )

                    DasUtil.getTables(dataSource)
                        .filter { schema.contains(it.dasParent) }
                        .forEach { dasTable ->
                            if (dasTable.name == parts[1]) {
                                table.add(dasTable)

                                column.addAll(
                                    DasUtil.getColumns(dasTable).filter { it.name == parts.last() }
                                )
                            } else if (tablesAndAliases[parts[1]]?.first == dasTable.name) {
                                table.add(dasTable)
                                alias = dasTable.name

                                column.addAll(
                                    DasUtil.getColumns(dasTable).filter { it.name == parts.last() }
                                )
                            }
                        }
                }
            }
        }
    }
}