package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class NamespacesExpectation(val fixture: CodeInsightTestFixture, val opposite: Boolean = false) : Expectation<NamespacesExpectation> {
    private val contains = !opposite
    override val not get() = NamespacesExpectation(fixture, opposite = true)
    override val but get() = NamespacesExpectation(fixture, opposite = false)
    override val and get() = this

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
