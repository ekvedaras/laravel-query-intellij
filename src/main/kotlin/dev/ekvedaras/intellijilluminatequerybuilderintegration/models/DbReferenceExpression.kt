package dev.ekvedaras.intellijilluminatequerybuilderintegration.models

import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.LaravelUtils
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.MethodUtils

class DbReferenceExpression(val expression: PsiElement, val type: Type) {
    companion object {
        enum class Type {
            Table,
            Column
        }
    }

    val allSchemas = mutableMapOf<String, DasNamespace>()
    val allTables = mutableMapOf<String, MutableMap<String, DasTable>>()
    val tablesAndAliases = mutableMapOf<String, String>()

    var schema: DasNamespace? = null
    var table: DasTable? = null
    var column: DasColumn? = null
    var alias: String? = null


    init {
        if (type == Type.Column) collectTablesAndAliases()
        resolveSchemasAndTables()
        findExpressionReferences()
    }

    private fun collectTablesAndAliases() {
        val method = MethodUtils.resolveMethodReference(expression) ?: return

        MethodUtils.findMethodsInTree(method.parentOfType<Statement>()!!.firstChild)
            .filter { LaravelUtils.BuilderTableMethods.contains(it.name) }
            .forEach loop@{
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
                    tablesAndAliases[_table.substringAfter("as").trim()] = _table.substringBefore("as").trim()
                    return@loop
                }

                if (!LaravelUtils.BuilderTableAliasParams.containsKey(it.name)) {
                    tablesAndAliases[_table] = _table
                    return@loop
                }

                val aliasParam: Int = LaravelUtils.BuilderTableAliasParams[it.name] ?: return@loop
                val alias: String? = (it.getParameter(aliasParam) as? StringLiteralExpressionImpl)?.contents

                tablesAndAliases[alias ?: _table] = _table
            }
    }

    private fun resolveSchemasAndTables() {
        DbUtil.getDataSources(expression.project).forEach { dataSource ->
            DasUtil.getSchemas(dataSource).forEach {
                allSchemas[it.name] = it
            }

            DasUtil.getTables(dataSource)
                .filter { !it.isSystem && it.dasParent != null }
                .forEach {
                    allTables.getOrPut(it.dasParent!!.name, { mutableMapOf() })[it.name] = it
                }
        }
    }

    private fun findExpressionReferences() {
        for (expressionPart in expression.text.trim('"').trim('\'').split(".")) {
            val part = expressionPart.replace("IntellijIdeaRulezzz", "").trim()

            if (schema == null && findSchema(part)) continue
            if (table == null && findTable(part)) continue
            if (column == null && type == Type.Column && findColumn(part)) continue
        }
    }

    private fun findSchema(part: String): Boolean {
        if (allSchemas.containsKey(part)) {
            schema = allSchemas[part]
            return true
        }

        return false
    }

    private fun findTable(part: String): Boolean {
        if (schema != null) {
            if (allTables[schema!!.name]!!.containsKey(part)) {
                table = allTables[schema!!.name]!![part]
                return true
            }
        } else {
            for ((schemaName, schemaTables) in allTables) {
                if (schemaTables.containsKey(part)) {
                    schema = allSchemas[schemaName]
                    table = schemaTables[part]
                    return true
                }
            }
        }

        return false
    }

    private fun findColumn(part: String): Boolean {
        if (table == null) {
            if (schema != null) {
                allTables[schema!!.name]!!.values.forEach { dasTable ->
                    val dasColumn = DasUtil.getColumns(dasTable).find { it.name == part }
                    if (dasColumn != null) {
                        schema = allSchemas[dasTable.dasParent!!.name]
                        table = dasTable
                        column = dasColumn
                        return true
                    }
                }
            } else {
                for ((schemaName, schemaTables) in allTables) {
                    schemaTables.values.forEach { dasTable ->
                        val dasColumn = DasUtil.getColumns(dasTable).find { it.name == part }
                        if (dasColumn != null) {
                            schema = allSchemas[schemaName]
                            table = dasTable
                            column = dasColumn
                            return true
                        }
                    }
                }
            }
        } else {
            val dasColumn = DasUtil.getColumns(table!!).find { it.name == part }
            if (dasColumn != null) {
                column = dasColumn

                if (schema == null) {
                    schema = allSchemas[table!!.name]
                }

                return true
            }
        }

        return false
    }
}