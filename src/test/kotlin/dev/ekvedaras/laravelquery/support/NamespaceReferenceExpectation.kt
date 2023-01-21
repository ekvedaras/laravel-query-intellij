package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.usageView.UsageInfo
import dev.ekvedaras.laravelquery.integration.query.reference.NamespaceReference

internal data class NamespaceReferenceExpectation(val namespace: Namespaces, override val fixture: CodeInsightTestFixture) : ReferenceExpectation<NamespaceReference> {
    override val name = namespace.name
    override val psiElement = namespace.find(fixture.project).asDbNamespace()
    override val referenceClass = NamespaceReference::class.java
    override var usage: UsageInfo? = null
}
