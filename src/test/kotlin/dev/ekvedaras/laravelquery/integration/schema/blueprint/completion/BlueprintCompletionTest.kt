package dev.ekvedaras.laravelquery.integration.schema.blueprint.completion

import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.support.Columns
import dev.ekvedaras.laravelquery.support.Namespaces
import dev.ekvedaras.laravelquery.support.Tables

internal class BlueprintCompletionTest : BaseTestCase() {
    fun testItCompletesInStringMethodCallOfExistingTable() {
        myFixture.configureByFile("integration/schema/blueprint/completion/inStringCallOfCreateCall.php")
        myFixture.completeBasic()

        Namespaces.expect(myFixture).not().toBeCompleted()
        Tables.users.expect(myFixture)
            .not().toBeCompleted()
            .but().toHaveItsColumnsCompleted().withoutOtherTables().andTheirColumns()
    }
}
