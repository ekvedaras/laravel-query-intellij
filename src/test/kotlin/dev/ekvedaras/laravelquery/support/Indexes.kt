package dev.ekvedaras.laravelquery.support

import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.domain.database.Index

internal enum class Indexes {
    usersTrashcan {
        override fun table() = Tables.users
        override fun indexName(): String = "trashcan"
    },

    customersBillableIndex {
        override fun table() = Tables.customers
        override fun indexName(): String = "customers_billable_id_billable_type_index"
    };

    abstract fun table(): Tables
    abstract fun indexName(): String
    fun expect(fixture: CodeInsightTestFixture) = IndexExpectation(this, fixture)
    fun find(project: Project): Index = table()
        .find(project)
        .findIndex(indexName())
        ?: throw Exception("Cannot find ${table().namespace().name}.${table().name}.${indexName()} index")

    companion object {
        fun expect(fixture: CodeInsightTestFixture) = IndexesExpectation(fixture)
        fun exceptFor(table: Tables) = values().filterNot { it.table() == table }
        fun CodeInsightTestFixture.expect(index: Indexes) = index.expect(this)
        fun CodeInsightTestFixture.expectIndexes() = Indexes.expect(this)
    }
}
