package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class TablesExpectation(val fixture: CodeInsightTestFixture, val opposite: Boolean = false) : Expectation<TablesExpectation> {
    private val contains = !opposite

    override val not get() = TablesExpectation(fixture, opposite = true)
    override val but get() = TablesExpectation(fixture, opposite = false)
    override val and get() = this

    fun toBeCompleted() = apply {
        BaseTestCase.assertLookup(
            contains,
            *Tables.values().map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }

    fun withNewTable(name: String) = apply {
        BaseTestCase.assertLookup(
            contains,
            name,
            inFixture = fixture,
        )
    }
}
