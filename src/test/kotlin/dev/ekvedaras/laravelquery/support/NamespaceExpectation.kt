package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.util.applyIf
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class NamespaceExpectation(val namespace: Namespaces, val fixture: CodeInsightTestFixture, val opposite: Boolean = false) : Expectation<NamespaceExpectation> {
    private val contains = !opposite
    private val doesNotContain = opposite

    override val not get() = NamespaceExpectation(namespace, fixture, opposite = true)
    override val but get() = NamespaceExpectation(namespace, fixture, opposite = false)
    override val and get() = this

    fun toBeReferenced() = NamespaceReferenceExpectation(namespace, fixture)

    fun toBeCompleted() = apply {
        BaseTestCase.assertLookup(
            contains,
            namespace.name,
            inFixture = fixture,
        )
    }

    fun asWellAsOthers() = apply {
        BaseTestCase.assertLookup(
            contains,
            *Namespaces.except(namespace).map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }

    fun exceptOthers() = apply { not.asWellAsOthers() }

    fun withTables() = apply {
        BaseTestCase.assertLookup(
            contains,
            *namespace.tables().map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }

    fun toHaveTablesCompleted() = withTables()

    fun withoutTables() = apply { not.withTables() }

    fun onlyFromThisNamespace() = apply {
        BaseTestCase.assertLookup(
            doesNotContain,
            *Tables.exceptFor(namespace).map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }

    fun withoutAnyColumns() = apply { Columns.expect(fixture).applyIf(!opposite) { not }.toBeCompleted() }
}
