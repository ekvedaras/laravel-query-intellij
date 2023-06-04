package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture

internal data class TableKeyExpectation(val tableKey: Keys, val fixture: CodeInsightTestFixture) {
    fun toBeReferenced() = TableKeyReferenceExpectation(tableKey, fixture)
}
