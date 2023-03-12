package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class TablesExpectation(val fixture: CodeInsightTestFixture, val not: Boolean = false) : Expectation<TablesExpectation> {
    private val contains = !not

    override fun not() = TablesExpectation(fixture, not = true)
    override fun but() = TablesExpectation(fixture, not = false)
    override fun and() = this

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
