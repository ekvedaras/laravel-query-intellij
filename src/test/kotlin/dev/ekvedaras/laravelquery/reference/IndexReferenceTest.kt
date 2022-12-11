package dev.ekvedaras.laravelquery.reference

import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbImplUtil
import com.intellij.database.util.DbUtil
import com.intellij.testFramework.UsefulTestCase
import dev.ekvedaras.laravelquery.BaseTestCase
import junit.framework.TestCase

internal class IndexReferenceTest : BaseTestCase() {
    fun testResolvesUniqueIndexReference() {
        myFixture.configureByFile("inspection/knownUniqueIndex.php")

        val index = DasUtil.getTables(dataSource())
            .first { it.name == "users" }
            .getDasChildren(ObjectKind.KEY)
            .first { it.name == "users_email_uindex" }
        val dbIndex = DbImplUtil.findElement(DbUtil.getDataSources(project).first(), index)
            ?: return fail("Failed to resolve DB index")

        val usages = myFixture.findUsages(dbIndex)

        UsefulTestCase.assertSize(1, usages)
        TestCase.assertEquals(KeyPsiReference::class.java, usages.first().referenceClass)
        TestCase.assertTrue(usages.first().element?.textMatches("'users_email_uindex'") ?: false)
        TestCase.assertEquals(111, usages.first().navigationRange.startOffset)
        TestCase.assertEquals(111 + index.name.length, usages.first().navigationRange.endOffset)
    }
}
