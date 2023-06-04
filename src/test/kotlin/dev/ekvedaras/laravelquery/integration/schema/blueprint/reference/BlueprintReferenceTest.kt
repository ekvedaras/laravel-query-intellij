package dev.ekvedaras.laravelquery.integration.schema.blueprint.reference

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Indexes
import dev.ekvedaras.laravelquery.support.Indexes.Companion.expect
import dev.ekvedaras.laravelquery.support.PrimaryKeys
import dev.ekvedaras.laravelquery.support.PrimaryKeys.Companion.expect

internal class BlueprintReferenceTest : BaseTestCase() {
    fun testItReferencesInIndexCallIndexNameParameter() {
        myFixture.configureByFile("integration/schema/blueprint/reference/inIndexCallIndexParameter.php")
        myFixture.completeBasic()

        myFixture.expect(Indexes.usersTrashcan).toBeReferenced().inString("'trashcan'")
    }

    fun testItReferencesInPrimaryCallNameParameter() {
        myFixture.configureByFile("integration/schema/blueprint/reference/inPrimaryCallNameParameter.php")
        myFixture.completeBasic()

        myFixture.expect(PrimaryKeys.usersId).toBeReferenced().inString("'users_id_primary'")
    }
}
