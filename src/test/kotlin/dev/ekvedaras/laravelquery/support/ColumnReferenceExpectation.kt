package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.usageView.UsageInfo
import dev.ekvedaras.laravelquery.integration.query.reference.ColumnReference

internal data class ColumnReferenceExpectation(val column: Columns, override val fixture: CodeInsightTestFixture) : ReferenceExpectation<ColumnReference> {
    override val name = column.columnName()
    override val psiElement = column.find(fixture.project).asDbColumn()
    override val referenceClass = ColumnReference::class.java
    override var usage: UsageInfo? = null
}
