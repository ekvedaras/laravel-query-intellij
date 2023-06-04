package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.usageView.UsageInfo
import dev.ekvedaras.laravelquery.integration.schema.blueprint.reference.TableKeyReference

internal data class TableKeyReferenceExpectation(val tableKeys: Keys, override val fixture: CodeInsightTestFixture) : ReferenceExpectation<TableKeyReference> {
    override val name = tableKeys.keyName()
    override val psiElement = tableKeys.find(fixture.project).asDbTableKey()
    override val referenceClass = TableKeyReference::class.java
    override var usage: UsageInfo? = null
}
