package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.usageView.UsageInfo
import dev.ekvedaras.laravelquery.integration.query.reference.TableReference

internal data class TableReferenceExpectation(val table: Tables, override val fixture: CodeInsightTestFixture) : ReferenceExpectation<TableReference> {
    override val name = table.name
    override val psiElement = table.find(fixture.project).asDbTable()
    override val referenceClass = TableReference::class.java
    override var usage: UsageInfo? = null
}
