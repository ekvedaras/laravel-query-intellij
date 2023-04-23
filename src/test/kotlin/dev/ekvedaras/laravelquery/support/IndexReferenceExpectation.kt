package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.usageView.UsageInfo
import dev.ekvedaras.laravelquery.integration.query.reference.IndexReference

internal data class IndexReferenceExpectation(val index: Indexes, override val fixture: CodeInsightTestFixture) : ReferenceExpectation<IndexReference> {
    override val name = index.indexName()
    override val psiElement = index.find(fixture.project).asDbIndex()
    override val referenceClass = IndexReference::class.java
    override var usage: UsageInfo? = null
}
