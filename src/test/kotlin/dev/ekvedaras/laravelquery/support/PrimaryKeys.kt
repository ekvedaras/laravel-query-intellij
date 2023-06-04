package dev.ekvedaras.laravelquery.support

import com.intellij.testFramework.fixtures.CodeInsightTestFixture

internal enum class PrimaryKeys : Keys {
    usersId {
        override fun table() = Tables.users
        override fun keyName(): String = "users_id_primary"
    },

    customersId {
        override fun table() = Tables.customers
        override fun keyName(): String = "customers_id_primary"
    },

    failedJobsId {
        override fun table() = Tables.failed_jobs
        override fun keyName(): String = "failed_jobs_id_primary"
    },

    migrationsId {
        override fun table() = Tables.migrations
        override fun keyName(): String = "migrations_id_primary"
    };

    fun expect(fixture: CodeInsightTestFixture) = TableKeyExpectation(this, fixture)

    companion object {
        fun expect(fixture: CodeInsightTestFixture) = PrimaryKeysExpectation(fixture)
        fun exceptFor(table: Tables) = values().filterNot { it.table() == table }
        fun CodeInsightTestFixture.expect(primaryKey: PrimaryKeys) = primaryKey.expect(this)
        fun CodeInsightTestFixture.expectPrimaryKeys() = PrimaryKeys.expect(this)
    }
}
