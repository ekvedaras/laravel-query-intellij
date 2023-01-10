package dev.ekvedaras.laravelquery.integration.query.reference

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.domain.database.Table

internal class QueryDatabaseElementReferenceTest : BaseTestCase() {
    fun testItResolvesColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/columnInGetCall.php")

        val userIdColumn = Table.findFirst("users", project)
            ?.findColumn("id")
            ?: return fail("Cannot find users.id column")

        val usages = myFixture.findUsages(userIdColumn.asDbColumn())

        assertSize(1, usages)
        assertEquals(ColumnReference::class.java, usages.first().referenceClass)
        assertTrue(usages.first().element?.textMatches("'id'") ?: false)
        assertEquals(82, usages.first().navigationRange.startOffset)
        assertEquals(82 + userIdColumn.name.length, usages.first().navigationRange.endOffset)
    }

    fun testItDoesNotResolvesWrongColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/columnInGetCall.php")

        val userEmailColumn = Table.findFirst("users", project)
            ?.findColumn("email")
            ?: return fail("Cannot find users.email column")

        assertEmpty(myFixture.findUsages(userEmailColumn.asDbColumn()))
    }

    fun testResolvesTableAndColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/tableAndColumnInGetCall.php")

        val usersTable = Table.findFirst("users", project) ?: return fail("Cannot find users table")
        val userIdColumn = usersTable.findColumn("id") ?: return fail("Cannot find users.id column")

        val tableUsages = myFixture.findUsages(usersTable.asDbTable())
        val columnUsages = myFixture.findUsages(userIdColumn.asDbColumn())

        assertSize(2, tableUsages) // from() + get()
        assertSize(1, columnUsages)

        assertEquals(TableReference::class.java, tableUsages.last().referenceClass)
        assertEquals(ColumnReference::class.java, columnUsages.first().referenceClass)

        assertTrue(tableUsages.last().element?.textMatches("'users.id'") ?: false)
        assertTrue(columnUsages.first().element?.textMatches("'users.id'") ?: false)

        assertEquals(82, tableUsages.last().navigationRange.startOffset)
        assertEquals(82 + usersTable.name.length + 1, columnUsages.first().navigationRange.startOffset)

        assertEquals(82 + usersTable.name.length, tableUsages.last().navigationRange.endOffset)
        assertEquals(
            82 + usersTable.name.length + 1 + userIdColumn.name.length,
            columnUsages.first().navigationRange.endOffset
        )
    }

    fun testResolvesNamespaceAndTableAndColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/namespaceAndTableAndColumnInGetCall.php")

        val usersTable = Table.findFirst("users", project) ?: return fail("Cannot find users table")
        val userIdColumn = usersTable.findColumn("id") ?: return fail("Cannot find users.id column")

        val namespaceUsages = myFixture.findUsages(usersTable.namespace.asDbNamespace())
        val tableUsages = myFixture.findUsages(usersTable.asDbTable())
        val columnUsages = myFixture.findUsages(userIdColumn.asDbColumn())

        assertSize(2, namespaceUsages) // from() + get()
        assertSize(2, tableUsages) // from() + get()
        assertSize(1, columnUsages)

        assertEquals(NamespaceReference::class.java, namespaceUsages.first().referenceClass)
        assertEquals(TableReference::class.java, tableUsages.first().referenceClass)
        assertEquals(ColumnReference::class.java, columnUsages.first().referenceClass)

        assertTrue(namespaceUsages.last().element?.textMatches("'testProject1.users.id'") ?: false)
        assertTrue(tableUsages.last().element?.textMatches("'testProject1.users.id'") ?: false)
        assertTrue(columnUsages.first().element?.textMatches("'testProject1.users.id'") ?: false)

        assertEquals(82, namespaceUsages.last().navigationRange.startOffset)
        assertEquals(82 + usersTable.namespace.name.length + 1, tableUsages.last().navigationRange.startOffset)
        assertEquals(
            82 + usersTable.namespace.name.length + 1 + usersTable.name.length + 1,
            columnUsages.first().navigationRange.startOffset
        )

        assertEquals(82 + usersTable.namespace.name.length, namespaceUsages.last().navigationRange.endOffset)
        assertEquals(
            82 + usersTable.namespace.name.length + 1 + usersTable.name.length,
            tableUsages.last().navigationRange.endOffset
        )
        assertEquals(
            82 + usersTable.namespace.name.length + 1 + usersTable.name.length + 1 + userIdColumn.name.length,
            columnUsages.first().navigationRange.endOffset
        )
    }

    fun testResolvesJsonColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/jsonColumnInGetCall.php")

        val userIdColumn = Table.findFirst("users", project)
            ?.findColumn("id")
            ?: return fail("Cannot find users.id column")

        val usages = myFixture.findUsages(userIdColumn.asDbColumn())

        assertSize(1, usages)
        assertEquals(ColumnReference::class.java, usages.first().referenceClass)
        assertTrue(usages.first().element?.textMatches("'id->prop'") ?: false)
        assertEquals(82, usages.first().navigationRange.startOffset)
        assertEquals(82 + userIdColumn.name.length, usages.first().navigationRange.endOffset)
    }
}
