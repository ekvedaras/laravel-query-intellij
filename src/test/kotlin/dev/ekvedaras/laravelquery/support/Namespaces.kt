package dev.ekvedaras.laravelquery.support

import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import dev.ekvedaras.laravelquery.BaseTestCase
import dev.ekvedaras.laravelquery.domain.database.Namespace

internal enum class Namespaces {
    testProject1 {
        override fun find(project: Project): Namespace = Namespace.findFirst("testProject1", project)
            ?: throw Exception("Cannot find testProject1 namespace")
    },

    testProject2 {
        override fun find(project: Project): Namespace = Namespace.findFirst("testProject2", project)
            ?: throw Exception("Cannot find testProject2 namespace")
    };


    abstract fun find(project: Project): Namespace

    fun tables() = Tables.values().filter { it.namespace() == this }
    fun expect(fixture: CodeInsightTestFixture) = NamespaceExpectation(this, fixture)

    fun assertIsSuggested(fixture: CodeInsightTestFixture) = this.apply { BaseTestCase.assertLookupContains(this.name, inFixture = fixture) }
    fun assertIsTheOnlyOneSuggested(fixture: CodeInsightTestFixture) = this.apply {
        this.assertIsSuggested(fixture)
        BaseTestCase.assertLookupDoesNotContain(
            *values().filterNot { it.name == this.name }.map { it.name }.toList().toTypedArray(),
            inFixture = fixture
        )
    }

    fun assertIsNotSuggested(fixture: CodeInsightTestFixture) = this.apply {
        BaseTestCase.assertLookupDoesNotContain(this.name, inFixture = fixture)
    }

    companion object {
        fun expect(fixture: CodeInsightTestFixture) = NamespacesExpectation(fixture)
        fun except(namespace: Namespaces) = values().filterNot { it == namespace }

        fun assertAllSuggested(fixture: CodeInsightTestFixture) = BaseTestCase.assertLookupContains(
            *values().map { it.name }.toList().toTypedArray(), inFixture = fixture
        )

        fun assertNoneAreSuggested(fixture: CodeInsightTestFixture) = BaseTestCase.assertLookupDoesNotContain(
            *values().map { it.name }.toList().toTypedArray(), inFixture = fixture
        )
    }
}
