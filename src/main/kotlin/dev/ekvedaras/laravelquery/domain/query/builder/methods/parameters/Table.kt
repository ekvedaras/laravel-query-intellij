package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.database.Table
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.AliasedParam.Companion.toAliasedParam

class Table(val element: StringLiteralExpression) {
    val name: String
    val namespace: String?
    val alias: String?

    init {
        val aliasedParam = element.text.toAliasedParam()
        val parts = aliasedParam.target.split('.').reversed()

        this.name = parts[0]
        this.namespace = parts.getOrNull(1)
        this.alias = aliasedParam.alias
    }

    fun asDbTable(): Table? = Table.findFirst(table = name, project = element.project)
}
