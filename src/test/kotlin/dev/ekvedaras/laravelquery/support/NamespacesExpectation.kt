package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class NamespacesExpectation(val fixture: CodeInsightTestFixture, val not: Boolean = false) : Expectation<NamespacesExpectation> {
    private val contains = !not
    override fun not() = NamespacesExpectation(fixture, not = true)
    override fun but() = NamespacesExpectation(fixture, not = false)
    override fun and() = this

    fun toBeCompleted() = apply {
        BaseTestCase.assertLookup(
            contains,
            *Namespaces.values().map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }

    fun withNewNamespace(name: String) = apply {
        BaseTestCase.assertLookup(
            contains,
            name,
            inFixture = fixture,
        )
    }
}
