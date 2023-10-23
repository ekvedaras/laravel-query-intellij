package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.usageView.UsageInfo
import dev.ekvedaras.laravelquery.integration.schema.blueprint.reference.IndexReference

internal data class IndexReferenceInSchemaBuilderExpectation(val index: Indexes, override val fixture: CodeInsightTestFixture) : ReferenceExpectation<IndexReference> {
    override val name = index.indexName()
    override val psiElement = index.find(fixture.project).asDbIndex()
    override val referenceClass = IndexReference::class.java
    override var usage: UsageInfo? = null
}
