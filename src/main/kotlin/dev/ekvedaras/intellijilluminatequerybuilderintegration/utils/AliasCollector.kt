package dev.ekvedaras.intellijilluminatequerybuilderintegration.utils

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import dev.ekvedaras.intellijilluminatequerybuilderintegration.models.DbReferenceExpression
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.intellijilluminatequerybuilderintegration.utils.DatabaseUtils.Companion.tables

class AliasCollector(private val reference: DbReferenceExpression) {
    fun extractAliasFromString(
        method: MethodReference,
        referencedTable: String,
        referencedSchema: String?,
    ) {
        var referencedSchema1 = referencedSchema
        val alias = referencedTable.substringAfter("as").trim()
        val table = referencedTable.substringBefore("as").trim()

        if (referencedSchema1 == null) {
            reference.project.dbDataSourcesInParallel().forEach loop@{ dataSource ->
                val dasTable = dataSource.tables().firstOrNull { it.name == table } ?: return@loop
                referencedSchema1 = dasTable.dasParent?.name
            }
        }

        reference.tablesAndAliases[alias] = table to referencedSchema1
        reference.aliases[table] = alias to method.getParameter(0)!!
    }

    fun resolveAliasFromParam(
        method: MethodReference,
        referencedTable: String,
        referencedSchema: String?
    ) {
        val aliasParam: Int = LaravelUtils.BuilderTableAliasParams[method.name] ?: return
        val alias: String? = (method.getParameter(aliasParam) as? StringLiteralExpressionImpl)?.contents

        reference.tablesAndAliases[alias ?: referencedTable] = referencedTable to referencedSchema

        if (alias != null && method.getParameter(aliasParam) != null) {
            reference.aliases[referencedTable] = alias to method.getParameter(aliasParam)!!
        }
    }
}