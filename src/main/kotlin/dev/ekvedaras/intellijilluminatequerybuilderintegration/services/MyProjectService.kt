package dev.ekvedaras.intellijilluminatequerybuilderintegration.services

import com.intellij.openapi.project.Project
import dev.ekvedaras.intellijilluminatequerybuilderintegration.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
