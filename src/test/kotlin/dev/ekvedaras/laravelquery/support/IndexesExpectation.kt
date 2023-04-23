package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class IndexesExpectation(val fixture: CodeInsightTestFixture, private val not: Boolean = false) : Expectation<IndexesExpectation> {
    private val contains = !not

    override fun not() = IndexesExpectation(fixture, not = true)
    override fun but() = IndexesExpectation(fixture, not = false)
    override fun and() = this

    fun toBeCompleted() = apply {
        BaseTestCase.assertLookup(
            contains,
            *Indexes.values().map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }
}
