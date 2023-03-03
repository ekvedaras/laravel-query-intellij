package dev.ekvedaras.laravelquery.domain.tests

import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import dev.ekvedaras.laravelquery.domain.StringParameter
import dev.ekvedaras.laravelquery.domain.tests.parameters.ColumnParameter
import dev.ekvedaras.laravelquery.domain.tests.parameters.TableParameter
import dev.ekvedaras.laravelquery.support.LaravelClasses
import dev.ekvedaras.laravelquery.support.isMemberOfAny

sealed interface TestMethodCall {
    val tableParameter: TableParameter?
    val columns: Set<ColumnParameter>
        get() = setOf()

    fun columnParameterFor(stringParameter: StringParameter): ColumnParameter? =
        columns.find { stringParameter.equals(it) }

    companion object {
        fun from(reference: MethodReference): TestMethodCall? {
            if (! reference.isMemberOfAny(LaravelClasses.TestCase)) return null

            return when (reference.name) {
                "assertDatabaseHas", "assertDatabaseMissing", "assertDeleted" -> AssertDatabaseHasCall(reference)
                "assertDatabaseCount", "assertDatabaseEmpty" -> AssertDatabaseCountCall(reference)
                else -> null
            }
        }
    }

    fun completeFor(parameter: StringParameter): List<LookupElement>
}
