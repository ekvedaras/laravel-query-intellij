package dev.ekvedaras.intellijilluminatequerybuilderintegration.reference

import com.intellij.database.util.DasUtil
import dev.ekvedaras.intellijilluminatequerybuilderintegration.BaseTestCase

class ModelTableReferenceTest : BaseTestCase() {
    fun testResolveTableNameFromModelTableProperty() {
        myFixture.configureByFile("model/modelWithTableProperty.php")

        val table = DasUtil.getTables(db)
            .filter { it.name == "users" }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val otherTable = DasUtil.getTables(db)
            .filterNot { it.name == "users" }
            .lastOrNull() ?: return

        val expected = schemas + columns
        val notExpected =
                    schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                        .flatten() +                                                                      // Tables of other schemas
                    DasUtil.getColumns(otherTable)
                        .filterNot { columns.contains(it.name) }
                        .map { it.name }                                                                  // Columns of other table

        myFixture.completeBasic()
        assertCompletion(*expected.toList().toTypedArray())
        assertNoCompletion(*notExpected.toList().toTypedArray())
    }

    fun testResolveTableNameModelName() {
        myFixture.configureByFile("model/modelWithoutTableProperty.php")

        val table = DasUtil.getTables(db)
            .filter { it.name == "users" }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val otherTable = DasUtil.getTables(db)
            .filterNot { it.name == "users" }
            .lastOrNull() ?: return

        val expected = schemas + columns
        val notExpected =
            schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                .flatten() +                                                                      // Tables of other schemas
                    DasUtil.getColumns(otherTable)
                        .filterNot { columns.contains(it.name) }
                        .map { it.name }                                                                  // Columns of other table

        myFixture.completeBasic()
        assertCompletion(*expected.toList().toTypedArray())
        assertNoCompletion(*notExpected.toList().toTypedArray())
    }
}