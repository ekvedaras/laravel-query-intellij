package dev.ekvedaras.laravelquery.domain.query.builder.methods

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.Parameter

interface MethodCall {
    val reference: MethodReference

    fun completeFor(parameter: Parameter): List<LookupElement>
}
