package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class IndexesExpectation(val fixture: CodeInsightTestFixture, private val opposite: Boolean = false) : Expectation<IndexesExpectation> {
    private val contains = !opposite

    override val not get() = IndexesExpectation(fixture, opposite = true)
    override val but get() = IndexesExpectation(fixture, opposite = false)
    override val and get() = this

    fun toBeCompleted() = apply {
        BaseTestCase.assertLookup(
            contains,
            *Indexes.values().map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }
}
