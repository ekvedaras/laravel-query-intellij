package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

class Column(val element: StringParameter) {
    val name: String
    val table: String?
    val namespace: String?
    val alias: String?

    init {
        val aliasedParam = element.toAliasedParam()
        val parts = aliasedParam.target.split('.').reversed()

        this.name = parts[0]
        this.table = parts.getOrNull(1)
        this.namespace = parts.getOrNull(2)
        this.alias = aliasedParam.alias
    }
}
