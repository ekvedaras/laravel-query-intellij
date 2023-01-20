package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class ColumnsExpectation(val fixture: CodeInsightTestFixture, val not: Boolean = false) : Expectation<ColumnsExpectation> {
    private val contains = !not

    override fun not() = ColumnsExpectation(fixture, not = true)
    override fun but() = ColumnsExpectation(fixture, not = false)
    override fun and() = this

    fun toBeCompleted() = apply {
        BaseTestCase.assertLookup(
            contains,
            *Columns.values().map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }
}
