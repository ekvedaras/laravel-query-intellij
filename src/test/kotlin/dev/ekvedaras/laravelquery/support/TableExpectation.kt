package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase

internal data class TableExpectation(val table: Tables, val fixture: CodeInsightTestFixture, val not: Boolean = false) : Expectation<TableExpectation> {
    private val contains = !not

    override fun not() = TableExpectation(table, fixture, not = true)
    override fun but() = TableExpectation(table, fixture, not = false)
    override fun and() = this

    fun toBeReferenced() = TableReferenceExpectation(table, fixture)

    fun toBeCompleted() = apply {
        BaseTestCase.assertLookup(
            contains,
            table.name,
            inFixture = fixture,
        )
    }

    fun asWellAsOthers() = apply {
        BaseTestCase.assertLookup(
            contains,
            *Tables.except(table).map { it.name }.toTypedArray(),
            inFixture = fixture,
        )
    }

    fun asWellAsOthersFromSameNamespace() = apply {
        table.namespace().expect(fixture).toHaveTablesCompleted()
    }

    fun exceptOthers() = apply { not().asWellAsOthers() }

    fun withoutOtherTables() = not().asWellAsOthers()

    fun withColumns() = apply {
        BaseTestCase.assertLookup(
            contains,
            *table.columns().map { it.columnName() }.toTypedArray(),
            inFixture = fixture,
        )
    }

    fun toHaveItsColumnsCompleted() = withColumns()

    fun toHaveAliasCompleted(alias: String) = apply {
        BaseTestCase.assertLookup(
            contains,
            alias,
            inFixture = fixture,
        )
    }

    fun andAlias(alias: String) = toHaveAliasCompleted(alias)

    fun andTheirColumns() = apply {
        BaseTestCase.assertLookup(
            contains,
            *Columns.exceptFor(table).map { it.columnName() }.toTypedArray(),
            inFixture = fixture,
        )
    }
}
