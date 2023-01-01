package dev.ekvedaras.laravelquery.domain.query

import com.intellij.openapi.project.DumbService
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable

data class QueryVariable(var variable: Variable, val query: Query) {
    fun usageStatements(): List<Statement> =
        if (DumbService.isDumb(variable.project))
            listOf()
        else ReferencesSearch.search(variable.originalElement, variable.resolveScope, false)
            .toList()
            .filterNot { it.element.originalElement == variable.originalElement }
            .mapNotNull { it.element.parentOfType() }
}
