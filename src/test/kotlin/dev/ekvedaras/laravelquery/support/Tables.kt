package dev.ekvedaras.laravelquery.support

import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.domain.database.Table

internal enum class Tables {
    users {
        override fun namespace() = Namespaces.testProject1
    },

    customers {
        override fun namespace() = Namespaces.testProject1
    },

    migrations {
        override fun namespace() = Namespaces.testProject2
    },

    failed_jobs {
        override fun namespace() = Namespaces.testProject2
    };

    abstract fun namespace(): Namespaces

    fun find(project: Project): Table = namespace()
        .find(project)
        .findTable(this.name)
        ?: throw Exception("Cannot find ${namespace().name}.${this.name} table")

    fun assertIsSuggested(fixture: CodeInsightTestFixture) = this.apply { BaseTestCase.assertLookupContains(this.name, inFixture = fixture) }
    fun assertIsTheOnlyOneSuggested(fixture: CodeInsightTestFixture) = this.apply {
        this.assertIsSuggested(fixture)
        BaseTestCase.assertLookupDoesNotContain(
            *values()
                .filterNot { it.name == this.name }
                .map { it.name }
                .toList()
                .toTypedArray(),
            inFixture = fixture,
        )
    }

    fun assertIsNotSuggested(fixture: CodeInsightTestFixture) = this.apply { BaseTestCase.assertLookupDoesNotContain(this.name, inFixture = fixture) }

    fun assertColumnsAreSuggested(fixture: CodeInsightTestFixture) = this.apply {
        BaseTestCase.assertLookupContains(
            *Columns.values()
                .filter { it.table() == this }
                .map { it.columnName() }
                .toList()
                .toTypedArray(),
            inFixture = fixture
        )
    }

    fun assertColumnsAreSuggestedOnlyForThisTable(fixture: CodeInsightTestFixture) = this.apply {
        this.assertColumnsAreSuggested(fixture)
        BaseTestCase.assertLookupDoesNotContain(
            *Columns.values()
                .filterNot { it.table() == this }
                .map { it.columnName() }
                .toList()
                .toTypedArray(),
            inFixture = fixture,
        )
    }

    companion object {
        fun assertAllSuggested(fixture: CodeInsightTestFixture) = BaseTestCase.assertLookupContains(
            *values().map { it.name }.toList().toTypedArray(), inFixture = fixture
        )

        fun assertNoneAreSuggested(fixture: CodeInsightTestFixture) = BaseTestCase.assertLookupDoesNotContain(
            *values().map { it.name }.toList().toTypedArray(), inFixture = fixture
        )
    }
}
