package dev.ekvedaras.laravelquery.reference

import com.intellij.database.util.DasUtil
import dev.ekvedaras.laravelquery.BaseTestCase

@Suppress("Deprecation")
internal class ModelReferenceTest : BaseTestCase() {
    fun testResolveTableNameFromModelTableProperty() {
        myFixture.configureByFile("model/modelWithTableProperty.php")

        val table = DasUtil.getTables(db)
            .filter { it.name == "users" }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val otherTable = DasUtil.getTables(db)
            .filterNot { it.name == "users" }
            .lastOrNull() ?: return fail("Did not find any tables.")

        val expected = schemas + columns
        val notExpected =
            schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                .flatten() + // Tables of other schemas
                DasUtil.getColumns(otherTable)
                    .filterNot { columns.contains(it.name) }
                    .map { it.name } // Columns of other table

        myFixture.completeBasic()
        assertCompletion(*expected.toList().toTypedArray())
        assertNoCompletion(*notExpected.toList().toTypedArray())
    }

    fun testResolveTableNameFromModelTablePropertyAndCompletesColumnsOnStaticWhere() {
        myFixture.configureByFile("model/modelWhere.php")

        val table = DasUtil.getTables(db)
            .filter { it.name == "users" }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val otherTable = DasUtil.getTables(db)
            .filterNot { it.name == "users" }
            .lastOrNull() ?: return fail("Did not find any tables.")

        val expected = schemas + columns
        val notExpected =
            schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                .flatten() + // Tables of other schemas
                DasUtil.getColumns(otherTable)
                    .filterNot { columns.contains(it.name) }
                    .map { it.name } // Columns of other table

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
            .lastOrNull() ?: return fail("Did not find any tables.")

        val expected = schemas + columns
        val notExpected =
            schemaTables.entries.filterNot { it.key == table.dasParent?.name }.map { it.value }
                .flatten() + // Tables of other schemas
                DasUtil.getColumns(otherTable)
                    .filterNot { columns.contains(it.name) }
                    .map { it.name } // Columns of other table

        myFixture.completeBasic()
        assertCompletion(*expected.toList().toTypedArray())
        assertNoCompletion(*notExpected.toList().toTypedArray())
    }

    fun testResolveRelationTableName() {
        myFixture.configureByFile("model/modelWithRelation.php")

        val users = DasUtil.getTables(db)
            .filter { it.name == "users" }
            .firstOrNull() ?: return fail("Did not find users table.")

        val customers = DasUtil.getTables(db)
            .filter { it.name == "customers" }
            .firstOrNull() ?: return fail("Did not find customers table.")

        val usersColumns = DasUtil.getColumns(users).map { it.name }
        val customersColumns = DasUtil.getColumns(customers).map { it.name }

        val otherTable = DasUtil.getTables(db)
            .filterNot { it.name == "users" || it.name == "customers" }
            .lastOrNull() ?: return fail("Did not find any tables.")

        val expected = schemas + usersColumns + customersColumns
        val notExpected =
            schemaTables.entries.filterNot { it.key == users.dasParent?.name }.map { it.value }
                .flatten() + // Tables of other schemas
                DasUtil.getColumns(otherTable)
                    .filterNot { usersColumns.contains(it.name) || customersColumns.contains(it.name) }
                    .map { it.name } // Columns of other table

        myFixture.completeBasic()
        assertCompletion(*expected.toList().toTypedArray())
        assertNoCompletion(*notExpected.toList().toTypedArray())
    }
}
