package dev.ekvedaras.laravelquery.integration.query.reference

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables

internal class QueryDatabaseElementReferenceTest : BaseTestCase() {
    fun testItResolvesColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/columnInGetCall.php")

        Columns.usersId
            .expect(myFixture)
            .toBeReferenced()
            .once()
            .at(82)
            .inString("id")
    }

    fun testItDoesNotResolvesWrongColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/columnInGetCall.php")

        Columns.usersEmail.expect(myFixture).toBeReferenced().never()
    }

    fun testResolvesTableAndColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/tableAndColumnInGetCall.php")

        Tables.users
            .expect(myFixture)
            .toBeReferenced()
            .twice()
            .first().at(68).inString("testProject1.users")
            .second().at(82).inString("users.id")

        Columns.usersId
            .expect(myFixture)
            .toBeReferenced()
            .once().at(82 + "${Tables.users.name}.".length).inString("users.id")
    }

    fun testResolvesNamespaceAndTableAndColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/namespaceAndTableAndColumnInGetCall.php")

        val usersTable = Tables.users.find(project)
        val userIdColumn = Columns.usersId.find(project)

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

        val userIdColumn = Columns.usersId.find(project)

        val usages = myFixture.findUsages(userIdColumn.asDbColumn())

        assertSize(1, usages)
        assertEquals(ColumnReference::class.java, usages.first().referenceClass)
        assertTrue(usages.first().element?.textMatches("'id->prop'") ?: false)
        assertEquals(82, usages.first().navigationRange.startOffset)
        assertEquals(82 + userIdColumn.name.length, usages.first().navigationRange.endOffset)
    }

    fun testItResolvesNamespaceTablesAndColumnsInJoinCall() {
        myFixture.configureByFile("integration/query/reference/joinCallWithAllParameters.php")

        val testProject1Namespace = Namespaces.testProject1.find(project)

        val usersTable = Tables.users.find(project)
        val userIdColumn = Columns.usersId.find(project)

        val customersTable = Tables.customers.find(project)
        val billableIdColumn = Columns.customersBillableId.find(project)

        val namespaceUsages = myFixture.findUsages(testProject1Namespace.asDbNamespace()).toList()
        val usersTableUsages = myFixture.findUsages(usersTable.asDbTable())
        val userIdColumnUsages = myFixture.findUsages(userIdColumn.asDbColumn())
        val customersTableUsages = myFixture.findUsages(customersTable.asDbTable())
        val billableIdColumnUsages = myFixture.findUsages(billableIdColumn.asDbColumn())

        assertSize(4, namespaceUsages)
        assertSize(2, usersTableUsages)
        assertSize(1, userIdColumnUsages)
        assertSize(2, customersTableUsages)
        assertSize(1, billableIdColumnUsages)

        assertEquals(NamespaceReference::class.java, namespaceUsages[0].referenceClass)
        assertTrue(namespaceUsages[0].element?.textMatches("'testProject1.users'") ?: false)
        assertEquals(60, namespaceUsages[0].navigationRange.startOffset)
        assertEquals(60 + testProject1Namespace.name.length, namespaceUsages[0].navigationRange.endOffset)

        assertEquals(TableReference::class.java, usersTableUsages.first().referenceClass)
        assertTrue(usersTableUsages.first().element?.textMatches("'testProject1.users'") ?: false)
        assertEquals(73, usersTableUsages.first().navigationRange.startOffset)
        assertEquals(73 + usersTable.name.length, usersTableUsages.first().navigationRange.endOffset)

        assertEquals(NamespaceReference::class.java, namespaceUsages[1].referenceClass)
        assertTrue(namespaceUsages[1].element?.textMatches("'testProject1.customers'") ?: false)
        assertEquals(93, namespaceUsages[1].navigationRange.startOffset)
        assertEquals(93 + customersTable.namespace.name.length, namespaceUsages[1].navigationRange.endOffset)

        assertEquals(TableReference::class.java, customersTableUsages.first().referenceClass)
        assertTrue(customersTableUsages.first().element?.textMatches("'testProject1.customers'") ?: false)
        assertEquals(106, customersTableUsages.first().navigationRange.startOffset)
        assertEquals(106 + customersTable.name.length, customersTableUsages.first().navigationRange.endOffset)

        assertEquals(NamespaceReference::class.java, namespaceUsages[2].referenceClass)
        assertTrue(namespaceUsages[2].element?.textMatches("'testProject1.customers.billable_id'") ?: false)
        assertEquals(119, namespaceUsages[2].navigationRange.startOffset)
        assertEquals(119 + customersTable.namespace.name.length, namespaceUsages[2].navigationRange.endOffset)

        assertEquals(TableReference::class.java, customersTableUsages.last().referenceClass)
        assertTrue(customersTableUsages.last().element?.textMatches("'testProject1.customers.billable_id'") ?: false)
        assertEquals(132, customersTableUsages.last().navigationRange.startOffset)
        assertEquals(132 + customersTable.name.length, customersTableUsages.last().navigationRange.endOffset)

        assertEquals(ColumnReference::class.java, billableIdColumnUsages.first().referenceClass)
        assertTrue(billableIdColumnUsages.first().element?.textMatches("'testProject1.customers.billable_id'") ?: false)
        assertEquals(142, billableIdColumnUsages.first().navigationRange.startOffset)
        assertEquals(142 + billableIdColumn.name.length, billableIdColumnUsages.first().navigationRange.endOffset)

        assertEquals(NamespaceReference::class.java, namespaceUsages[3].referenceClass)
        assertTrue(namespaceUsages[3].element?.textMatches("'testProject1.users.id'") ?: false)
        assertEquals(157, namespaceUsages[3].navigationRange.startOffset)
        assertEquals(157 + testProject1Namespace.name.length, namespaceUsages[3].navigationRange.endOffset)

        assertEquals(TableReference::class.java, usersTableUsages.last().referenceClass)
        assertTrue(usersTableUsages.last().element?.textMatches("'testProject1.users.id'") ?: false)
        assertEquals(170, usersTableUsages.last().navigationRange.startOffset)
        assertEquals(170 + usersTable.name.length, usersTableUsages.last().navigationRange.endOffset)

        assertEquals(ColumnReference::class.java, userIdColumnUsages.first().referenceClass)
        assertTrue(userIdColumnUsages.first().element?.textMatches("'testProject1.users.id'") ?: false)
        assertEquals(176, userIdColumnUsages.first().navigationRange.startOffset)
        assertEquals(176 + userIdColumn.name.length, userIdColumnUsages.first().navigationRange.endOffset)
    }
}
