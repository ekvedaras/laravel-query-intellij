package dev.ekvedaras.laravelquery.v4.reference

import com.intellij.database.util.DasUtil
import com.intellij.testFramework.TestDataFile
import dev.ekvedaras.laravelquery.BaseTestCase

internal class ModelReferenceTest : BaseTestCase() {
    fun testResolveTableNameFromModelTableProperty() {
        this.assertResolvesUsersTable("v4/model/modelWithTableProperty.php")
    }

    fun testResolveTableNameFromModelParentTableProperty() {
        this.assertResolvesUsersTable("v4/model/modelParentWithTableProperty.php")
    }

    fun testResolveTableNameFromModelWhenParentDoesNotHaveTableProperty() {
        this.assertResolvesUsersTable("v4/model/modelParentWithoutTableProperty.php")
    }

    fun testResolveTableNameFromModelTablePropertyAndCompletesColumnsOnStaticWhere() {
        this.assertResolvesUsersTable("v4/model/modelWhere.php")
    }

    fun testResolveTableNameModelName() {
        this.assertResolvesUsersTable("v4/model/modelWithoutTableProperty.php")
    }

    fun testResolveTableNameInsideScopeMethod() {
        this.assertResolvesUsersTable("v4/model/modelInsideScope.php")
    }

    fun testResolveRelationTableName() {
        myFixture.configureByFile("v4/model/modelWithRelation.php")

        val users = DasUtil.getTables(dataSource())
            .filter { it.name == "users" }
            .firstOrNull() ?: return fail("Did not find users table.")

        val customers = DasUtil.getTables(dataSource())
            .filter { it.name == "customers" }
            .firstOrNull() ?: return fail("Did not find customers table.")

        val usersColumns = DasUtil.getColumns(users).map { it.name }
        val customersColumns = DasUtil.getColumns(customers).map { it.name }

        val otherTable = DasUtil.getTables(dataSource())
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

    private fun assertResolvesUsersTable(@TestDataFile filePath : String) {
        myFixture.configureByFile(filePath)

        val table = DasUtil.getTables(dataSource())
            .filter { it.name == "users" }
            .firstOrNull() ?: return fail("Did not find any tables.")
        val columns = DasUtil.getColumns(table).map { it.name }
        val otherTable = DasUtil.getTables(dataSource())
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
}
