package dev.ekvedaras.laravelquery

import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.domain.database.Table

internal enum class Tables {
    users {
        override fun find(project: Project): Table = Namespaces.testProject1.find(project).findTable("users")
            ?: throw Exception("Cannot find testProject1.users table")
    },

    customers {
        override fun find(project: Project): Table = Namespaces.testProject1.find(project).findTable("customers")
            ?: throw Exception("Cannot find testProject1.customers table")
    },

    migrations {
        override fun find(project: Project): Table = Namespaces.testProject2.find(project).findTable("migrations")
            ?: throw Exception("Cannot find testProject2.migrations table")
    },

    failedJobs {
        override fun find(project: Project): Table = Namespaces.testProject2.find(project).findTable("failed_jobs")
            ?: throw Exception("Cannot find testProject2.failed_jobs table")
    };

    abstract fun find(project: Project): Table
}
