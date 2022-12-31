package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

data class AliasedParam(private val value: StringParameter) {
    // TODO retain upper cases
    val target: String = value.text.lowercase().substringBefore(" as ").trim()
    val alias: String? = if (value.text.contains(" as ", ignoreCase = true)) {
            value.text.lowercase().substringAfter(" as ").trim()
        } else {
            null
        }
}
