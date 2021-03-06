package dev.ekvedaras.laravelquery.services

import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
