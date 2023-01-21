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
            .once().at(82).inString("id")
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
            .once().at(88).inString("users.id")
    }

    fun testResolvesNamespaceAndTableAndColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/namespaceAndTableAndColumnInGetCall.php")

        Namespaces.testProject1
            .expect(myFixture)
            .toBeReferenced()
            .twice()
            .first().at(55).inString("testProject1.users")
            .second().at(82).inString("testProject1.users.id")

        Tables.users
            .expect(myFixture)
            .toBeReferenced()
            .twice()
            .first().at(68).inString("testProject1.users")
            .second().at(95).inString("testProject1.users.id")

        Columns.usersId
            .expect(myFixture)
            .toBeReferenced()
            .once().at(101).inString("testProject1.users.id")
    }

    fun testResolvesJsonColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/jsonColumnInGetCall.php")

        Columns.usersId
            .expect(myFixture)
            .toBeReferenced()
            .once().at(82).inString("id->prop")
    }

    fun testItResolvesNamespaceTablesAndColumnsInJoinCall() {
        myFixture.configureByFile("integration/query/reference/joinCallWithAllParameters.php")

        Namespaces.testProject1
            .expect(myFixture)
            .toBeReferenced()
            .times(4)
            .first().at(60).inString("testProject1.users")
            .second().at(93).inString("testProject1.customers")
            .then().at(119).inString("testProject1.customers.billable_id")
            .finally().at(157).inString("testProject1.users.id")

        Tables.users
            .expect(myFixture)
            .toBeReferenced()
            .twice()
            .first().at(73).inString("testProject1.users")
            .second().at(170).inString("testProject1.users.id")

        Tables.customers
            .expect(myFixture)
            .toBeReferenced()
            .twice()
            .first().at(106).inString("testProject1.customers")
            .second().at(132).inString("testProject1.customers.billable_id")

        Columns.usersId
            .expect(myFixture)
            .toBeReferenced()
            .once().at(176).inString("testProject1.users.id")

        Columns.customersBillableId
            .expect(myFixture)
            .toBeReferenced()
            .once().at(142).inString("testProject1.customers.billable_id")
    }
}
