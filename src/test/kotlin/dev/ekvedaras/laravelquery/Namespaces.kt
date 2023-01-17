package dev.ekvedaras.laravelquery

import com.intellij.openapi.project.Project
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
}
