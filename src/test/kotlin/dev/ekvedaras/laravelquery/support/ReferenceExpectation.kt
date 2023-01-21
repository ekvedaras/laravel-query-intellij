package dev.ekvedaras.laravelquery.support

import com.intellij.psi.PsiElement
import com.intellij.testFramework.UsefulTestCase
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.usageView.UsageInfo

sealed interface ReferenceExpectation<T> {
    val fixture: CodeInsightTestFixture
    val name: String
    val psiElement: PsiElement
    val referenceClass: Class<T>

    val usages: Collection<UsageInfo>
        get() = fixture
            .findUsages(psiElement)
            .onEach { UsefulTestCase.assertEquals(referenceClass, it.referenceClass) }
    var usage: UsageInfo?

    fun once() = times(1).first()
    fun twice() = times(2)
    fun times(number: Int) = apply { UsefulTestCase.assertSize(number, usages) }
    fun never() = UsefulTestCase.assertEmpty(usages)
    fun nth(n: Int) = apply { usage = usages.elementAt(n) }
    fun first() = nth(0)
    fun second() = nth(1)
    fun then() = nth(usages.indexOf(usage) + 1)
    fun finally() = nth(usages.size - 1)

    fun at(position: Int) = apply {
        usage.tap {
            UsefulTestCase.assertEquals(position, it.navigationRange.startOffset)
            UsefulTestCase.assertEquals(position + name.length, it.navigationRange.endOffset)
        }
    }

    fun inString(contents: String) = apply {
        usage.tap {
            BasePlatformTestCase.assertTrue(it.element?.textMatches("'${contents}'") ?: false)
        }
    }
}
