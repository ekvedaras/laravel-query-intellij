package dev.ekvedaras.laravelquery.domain.Query.Builder.Methods.Parameters

import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class Table(val element: StringLiteralExpression) {
    val name: String
    val database: String?
    val alias: String?

    init {
        val reference: String;

        if (element.text.contains("as", ignoreCase = true)) {
            reference = element.text.lowercase().substringBefore("as").trim()
            this.alias = element.text.lowercase().substringAfter("as").trim()
        } else {
            reference = element.text.lowercase()
            this.alias = null
        }

        val parts = reference.split('.').reversed()

        this.name = parts[0]
        this.database = parts.getOrNull(1)
    }
}
