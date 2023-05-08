package dev.ekvedaras.laravelquery.support

import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.domain.database.Column

internal enum class Columns {
    usersFirstName {
        override fun table() = Tables.users
        override fun columnName(): String = "first_name"
    },

    usersEmail {
        override fun table() = Tables.users
        override fun columnName(): String = "email"
    },

    customersBillableId {
        override fun table() = Tables.customers
        override fun columnName(): String = "billable_id"
    },

    migrationsBatch {
        override fun table() = Tables.migrations
        override fun columnName(): String = "batch"
    },

    failedJobsQueue {
        override fun table() = Tables.failed_jobs
        override fun columnName() = "queue"
    };

    abstract fun table(): Tables
    abstract fun columnName(): String
    fun expect(fixture: CodeInsightTestFixture) = ColumnExpectation(this, fixture)
    fun find(project: Project): Column = table()
        .find(project)
        .findColumn(columnName())
        ?: throw Exception("Cannot find ${table().namespace().name}.${table().name}.${columnName()} column")

    companion object {
        fun expect(fixture: CodeInsightTestFixture) = ColumnsExpectation(fixture)
        fun exceptFor(table: Tables) = values().filterNot { it.table() == table }
        fun CodeInsightTestFixture.expect(column: Columns) = column.expect(this)
        fun CodeInsightTestFixture.expectColumns() = Columns.expect(this)
    }
}
