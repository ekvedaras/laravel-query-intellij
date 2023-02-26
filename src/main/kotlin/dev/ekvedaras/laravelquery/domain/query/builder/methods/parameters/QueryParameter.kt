package dev.ekvedaras.laravelquery.domain.query.builder.methods.parameters

import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Parameter
import com.jetbrains.php.lang.psi.elements.Statement
import dev.ekvedaras.laravelquery.support.whenSmart

data class QueryParameter(val parameter: Parameter) {
    fun usageStatements(): List<Statement> = parameter.whenSmart {
        ReferencesSearch.search(parameter.originalElement, parameter.resolveScope, false).toList().filterNot { it.element.originalElement == parameter.originalElement }.mapNotNull { it.element.parentOfType() }
    } ?: listOf()
}
