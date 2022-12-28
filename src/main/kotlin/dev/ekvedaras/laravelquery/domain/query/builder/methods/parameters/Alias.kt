package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class Alias(val element: StringLiteralExpression) {
    val name: String = element.text
}
