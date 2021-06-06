package dev.ekvedaras.laravelquery.schema

import com.intellij.database.model.DasIndex
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.DasUtil
import dev.ekvedaras.laravelquery.BaseTestCase

internal class BlueprintIndexesTest : BaseTestCase() {
    fun testCompletesUniqueIndexesForDrop() {
        val table = DasUtil.getTables(dataSource()).first { it.name == "users" }

        myFixture.configureByFile("schema/dropUniqueIndex.php")
        myFixture.completeBasic()

        assertCompletion(
            *table.getDasChildren(ObjectKind.INDEX)
                .filter { (it as DasIndex).isUnique }
                .map { it.name }
                .toList().toTypedArray()
        )
        assertNoCompletion(*table.getDasChildren(ObjectKind.COLUMN).map { it.name }.toList().toTypedArray())
        assertNoCompletion(*schemas.toTypedArray())
        assertNoCompletion(*schemaTables.values.flatten().toTypedArray())
    }

    fun testCompletesKeysForDrop() {
        val table = DasUtil.getTables(dataSource()).first { it.name == "users" }

        myFixture.configureByFile("schema/dropPrimary.php")
        myFixture.completeBasic()

        assertCompletion(*table.getDasChildren(ObjectKind.KEY).map { it.name }.toList().toTypedArray())
        assertNoCompletion(*table.getDasChildren(ObjectKind.COLUMN).map { it.name }.toList().toTypedArray())
        assertNoCompletion(*schemas.toTypedArray())
        assertNoCompletion(*schemaTables.values.flatten().toTypedArray())
    }

    fun testCompletesColumnsInsteadOfIndexesForDropWithArray() {
        val table = DasUtil.getTables(dataSource()).first { it.name == "users" }

        myFixture.configureByFile("schema/dropIndexWithArray.php")
        myFixture.completeBasic()

        assertCompletion(*table.getDasChildren(ObjectKind.COLUMN).map { it.name }.toList().toTypedArray())
        assertNoCompletion(*table.getDasChildren(ObjectKind.INDEX).map { it.name }.toList().toTypedArray())
        assertNoCompletion(*schemas.toTypedArray())
        assertNoCompletion(*schemaTables.values.flatten().toTypedArray())
    }
}
