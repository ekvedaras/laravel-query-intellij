package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture

internal data class ColumnExpectation(val column: Columns, val fixture: CodeInsightTestFixture) {
    fun toBeReferenced() = ColumnReferenceExpectation(column, fixture)
}
