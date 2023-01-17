package dev.ekvedaras.laravelquery

import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.domain.database.Column

internal enum class Columns {
    usersId {
        override fun find(project: Project): Column = Tables.users.find(project).findColumn("id")
            ?: throw Exception("Cannot find testProject1.users.id column")
    },

    usersEmail {
        override fun find(project: Project): Column = Tables.users.find(project).findColumn("email")
            ?: throw Exception("Cannot find testProject1.users.email column")
    },

    customersBillableId {
        override fun find(project: Project): Column = Tables.customers.find(project).findColumn("billable_id")
            ?: throw Exception("Cannot find testProject1.customers.billable_id column")
    },

    migrationsBatch {
        override fun find(project: Project): Column = Tables.migrations.find(project).findColumn("batch")
            ?: throw Exception("Cannot find testProject2.migrations.batch column")
    },

    failedJobsQueue {
        override fun find(project: Project): Column = Tables.failed_jobs.find(project).findColumn("queue")
            ?: throw Exception("Cannot find testProject2.failed_jobs.queue column")
    };

    abstract fun find(project: Project): Column
}
