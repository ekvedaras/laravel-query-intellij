package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup

data class StringParameter(val element: StringLiteralExpression) {
    val text = element.text.unquoteAndCleanup()
    val isEmpty = text.isEmpty()
    val parts = text.split('.')
    val hasOnePart = parts.size == 1
    val hasTwoParts = parts.size == 2
    val hasThreeParts = parts.size == 3
    val hasUncompletedPart = parts.size > 1 && text.endsWith('.')
}
