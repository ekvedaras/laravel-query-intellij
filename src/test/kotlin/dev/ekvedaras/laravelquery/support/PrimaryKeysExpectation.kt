package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class PrimaryKeysExpectation(val fixture: CodeInsightTestFixture, private val opposite: Boolean = false) : Expectation<PrimaryKeysExpectation> {
    private val contains = !opposite

    override val not get() = PrimaryKeysExpectation(fixture, opposite = true)
    override val but get() = PrimaryKeysExpectation(fixture, opposite = false)
    override val and get() = this

    fun toBeCompleted() = apply {
        BaseTestCase.assertLookup(
            contains,
            *PrimaryKeys.values().map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }
}
