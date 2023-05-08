package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.support.Columns.Companion.expectColumns
import dev.ekvedaras.laravelquery.support.Indexes.Companion.expectIndexes
import dev.ekvedaras.laravelquery.support.Namespaces.Companion.expectNamespaces
import dev.ekvedaras.laravelquery.support.Tables.Companion.expectTables

internal data class DatabaseElementsExpectation(val fixture: CodeInsightTestFixture)
{
    val namespaces get() = fixture.expectNamespaces()
    val tables get() = fixture.expectTables()
    val columns get() = fixture.expectColumns()
    val indexes get() = fixture.expectIndexes()

    companion object {
        val CodeInsightTestFixture.expect get() = DatabaseElementsExpectation(this)
    }
}
