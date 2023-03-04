package dev.ekvedaras.laravelquery.domain.model

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.jetbrains.php.PhpIcons
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.GroupStatement
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.support.callChainOfType
import dev.ekvedaras.laravelquery.support.firstChildOfType
import dev.ekvedaras.laravelquery.support.substringAfter
import dev.ekvedaras.laravelquery.support.transformInstanceOf
import icons.DatabaseIcons

data class Scope(val method: Method, val model: Model) {
    init {
        if (! method.name.startsWith("scope", ignoreCase = true)) throw Exception("Method ${method.name} is not a scope method")
        if (method.containingClass != model.clazz) throw Exception("Method ${method.name} does not belong to model ${model.name}")
    }

    val name = method.name.substringAfter("scope", ignoreCase = true).replaceFirstChar { it.lowercase() }

    fun asLookupElement() =
        LookupElementBuilder.create(method, name)
            .withLookupString(name)
            .withTailText(model.name, true)
            .withTypeText(model.table?.nameWithoutPrefix, true)
            .withIcon(PhpIcons.METHOD_ICON)
}
