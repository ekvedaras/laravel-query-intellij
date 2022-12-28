package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.intellij.database.model.DasTable
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.AliasedParam.Companion.toAliasedParam
import dev.ekvedaras.laravelquery.support.Database

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

    fun asDasTable(): DasTable? = Database.findFirstTable(element.project, this.name, this.namespace)
}
