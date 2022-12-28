package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

data class AliasedParam(private val value: String) {
    companion object {
        fun String.toAliasedParam() = AliasedParam(this)
    }

    val target: String = value.lowercase().substringBefore(" as ").trim()
    val alias: String? = if (value.contains(" as ", ignoreCase = true)) {
            value.lowercase().substringAfter(" as ").trim()
        } else {
            null
        }
}
