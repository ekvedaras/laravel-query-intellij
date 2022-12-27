package dev.ekvedaras.laravelquery.domain.Query.Builder.Methods.Parameters

import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class Alias(val element: StringLiteralExpression) {
    val name: String = element.text
}
