package com.github.ekvedaras.intellijilluminatequerybuilderintegration.services

import com.intellij.openapi.project.Project
import com.github.ekvedaras.intellijilluminatequerybuilderintegration.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
