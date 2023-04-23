package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture

internal data class IndexExpectation(val index: Indexes, val fixture: CodeInsightTestFixture) {
    fun toBeReferenced() = IndexReferenceExpectation(index, fixture)
}
