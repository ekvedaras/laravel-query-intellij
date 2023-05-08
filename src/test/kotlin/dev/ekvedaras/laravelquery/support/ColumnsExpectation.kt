package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class ColumnsExpectation(val fixture: CodeInsightTestFixture, private val opposite: Boolean = false) : Expectation<ColumnsExpectation> {
    private val contains = !opposite

    override val not get() = ColumnsExpectation(fixture, opposite = true)
    override val but get() = ColumnsExpectation(fixture, opposite = false)
    override val and get() = this

    fun toBeCompleted() = apply {
        BaseTestCase.assertLookup(
            contains,
            *Columns.values().map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }
}
