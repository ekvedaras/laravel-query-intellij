package dev.ekvedaras.laravelquery.utils

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import dev.ekvedaras.laravelquery.models.DbReferenceExpression
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.dbDataSourcesInParallel
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.nameWithoutPrefix
import dev.ekvedaras.laravelquery.utils.DatabaseUtils.Companion.tables
import dev.ekvedaras.laravelquery.utils.LaravelUtils.Companion.canHaveAliasParam

class AliasCollector(private val reference: DbReferenceExpression) {
    fun extractAliasFromString(
        method: MethodReference,
        referencedTable: String,
        referencedSchema: String?,
    ) {
        var referencedSchema1 = referencedSchema
        val alias = referencedTable.substringAfter(" as ").substringAfter(" AS ").trim()
        val table = referencedTable.substringBefore(" as ").substringBefore(" AS ").trim()

        if (referencedSchema1 == null) {
            reference.project.dbDataSourcesInParallel().forEach loop@{ dataSource ->
                val dasTable = dataSource.tables().firstOrNull { it.nameWithoutPrefix(reference.project) == table } ?: return@loop
                referencedSchema1 = dasTable.dasParent?.name
            }
        }

        reference.tablesAndAliases[alias] = table to referencedSchema1

        val firstParam = method.getParameter(0) ?: return

        reference.aliases[table] = alias to firstParam
    }

    fun collectAliasFromMethodReference(
        method: MethodReference,
        referencedTable: String,
        referencedSchema: String?
    ) {
        if (!method.canHaveAliasParam()) {
            reference.tablesAndAliases[referencedTable] = referencedTable to referencedSchema
            return
        }

        resolveAliasFromParam(method, referencedTable, referencedSchema)
    }

    private fun resolveAliasFromParam(
        method: MethodReference,
        referencedTable: String,
        referencedSchema: String?
    ) {
        val aliasParam: Int = LaravelUtils.BuilderTableAliasParams[method.name] ?: return
        val alias: String? = (method.getParameter(aliasParam) as? StringLiteralExpressionImpl)?.contents

        reference.tablesAndAliases[alias ?: referencedTable] = referencedTable to referencedSchema

        if (alias != null && method.getParameter(aliasParam) != null) {
            val firstParam = method.getParameter(aliasParam) ?: return
            reference.aliases[referencedTable] = alias to firstParam
        }
    }
}
