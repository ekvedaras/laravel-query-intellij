package dev.ekvedaras.laravelquery.domain.schema.builder.methods

import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.lang.psi.elements.Parameter
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.Statement
import com.jetbrains.php.lang.psi.elements.Variable
import dev.ekvedaras.laravelquery.domain.model.Model
import dev.ekvedaras.laravelquery.domain.query.QueryStatement
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.isChildOfAny
import dev.ekvedaras.laravelquery.support.referenceVariable
import dev.ekvedaras.laravelquery.support.resolveClassesFromType
import dev.ekvedaras.laravelquery.support.tryTransformingInstanceOfOrContinue
import dev.ekvedaras.laravelquery.support.tryTransformingInstanceOfUnless
import dev.ekvedaras.laravelquery.support.whenSmart

data class BlueprintVariable(val variable: Parameter) {
    val clazz = variable.whenSmart { variable.resolveClassesFromType().firstOrNull() }
        ?: throw Exception("Cannot find a class of blueprint variable")

    init {
        if (!clazz.isChildOfAny(LaravelClasses.Blueprint, orIsAny = true)) throw Exception("Variable ${variable.name} is not an instance of Blueprint")
    }

    fun usageStatements(): List<Statement> = variable.whenSmart {
        ReferencesSearch.search(variable.originalElement, variable.resolveScope, false)
            .toList()
            .filterNot { it.element.originalElement == variable.originalElement }
            .mapNotNull { it.element.parentOfType() }
    } ?: listOf()
}
