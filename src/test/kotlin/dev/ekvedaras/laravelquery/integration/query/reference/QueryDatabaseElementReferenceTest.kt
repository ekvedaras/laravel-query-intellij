package dev.ekvedaras.laravelquery.integration.query.reference

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables

internal class QueryDatabaseElementReferenceTest : BaseTestCase() {
    fun testItResolvesColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/columnInGetCall.php")

        Columns.usersEmail
            .expect(myFixture)
            .toBeReferenced()
            .once().at(82).inString("email")
    }

    fun testItDoesNotResolvesWrongColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/columnInGetCall.php")

        Columns.usersFirstName.expect(myFixture).toBeReferenced().never()
    }

    fun testResolvesTableAndColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/tableAndColumnInGetCall.php")

        Tables.users
            .expect(myFixture)
            .toBeReferenced()
            .twice()
            .first().at(68).inString("testProject1.users")
            .second().at(82).inString("users.email")

        Columns.usersEmail
            .expect(myFixture)
            .toBeReferenced()
            .once().at(88).inString("users.email")
    }

    fun testResolvesNamespaceAndTableAndColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/namespaceAndTableAndColumnInGetCall.php")

        Namespaces.testProject1
            .expect(myFixture)
            .toBeReferenced()
            .twice()
            .first().at(55).inString("testProject1.users")
            .second().at(82).inString("testProject1.users.email")

        Tables.users
            .expect(myFixture)
            .toBeReferenced()
            .twice()
            .first().at(68).inString("testProject1.users")
            .second().at(95).inString("testProject1.users.email")

        Columns.usersEmail
            .expect(myFixture)
            .toBeReferenced()
            .once().at(101).inString("testProject1.users.email")
    }

    fun testResolvesJsonColumnInGetCall() {
        myFixture.configureByFile("integration/query/reference/jsonColumnInGetCall.php")

        Columns.usersEmail
            .expect(myFixture)
            .toBeReferenced()
            .once().at(82).inString("email->prop")
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
            .finally().at(157).inString("testProject1.users.email")

        Tables.users
            .expect(myFixture)
            .toBeReferenced()
            .twice()
            .first().at(73).inString("testProject1.users")
            .second().at(170).inString("testProject1.users.email")

        Tables.customers
            .expect(myFixture)
            .toBeReferenced()
            .twice()
            .first().at(106).inString("testProject1.customers")
            .second().at(132).inString("testProject1.customers.billable_id")

        Columns.usersEmail
            .expect(myFixture)
            .toBeReferenced()
            .once().at(176).inString("testProject1.users.email")

        Columns.customersBillableId
            .expect(myFixture)
            .toBeReferenced()
            .once().at(142).inString("testProject1.customers.billable_id")
    }
}
