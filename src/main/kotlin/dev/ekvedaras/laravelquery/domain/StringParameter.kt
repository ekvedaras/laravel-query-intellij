package dev.ekvedaras.laravelquery.domain

import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters.AliasedParam
import dev.ekvedaras.laravelquery.utils.PsiUtils.Companion.unquoteAndCleanup

data class StringParameter(val element: StringLiteralExpression) {
    val project = element.project
    val text = element.text.unquoteAndCleanup()
    val isEmpty = text.isEmpty()
    val parts = text.split('.')
    val hasOnePart = parts.size == 1
    val hasTwoParts = parts.size == 2
    val hasThreeParts = parts.size == 3
    val hasUncompletedPart = parts.size > 1 && text.endsWith('.')

    val isMethodReferenceParameter = element.parent.parent is MethodReference
    val isEntryOfArrayWhichIsMethodReferenceParameter = element.parent.parent is ArrayCreationExpression && element.parent.parent.parent.parent is MethodReference
    val isArrayHashKeyOfArrayWhichIsMethodReferenceParameter = element.parent.parent is ArrayHashElement && (element.parent.parent as ArrayHashElement).key == element && element.parent.parent.parent.parent.parent is MethodReference

    val parentMethodParameter = when (element.parent.parent) {
        is ArrayHashElement -> {
            element.parentOfType<ParameterList>()?.parameters?.find { it == element.parent.parent.parent }
        }

        is ArrayCreationExpression -> {
            element.parentOfType<ParameterList>()?.parameters?.find { it == element.parent.parent }
        }

        else -> element
    }

    fun toAliasedParam(): AliasedParam = AliasedParam(this)
}
