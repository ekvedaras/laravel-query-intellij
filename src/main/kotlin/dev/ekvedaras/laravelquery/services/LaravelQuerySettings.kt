package dev.ekvedaras.laravelquery.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import org.jetbrains.annotations.Nullable

@State(name = "LaravelQuerySettings", storages = [Storage("laravel-query-settings.xml")])
class LaravelQuerySettings(val project: Project) : PersistentStateComponent<LaravelQuerySettings> {
    var filterDataSources = false
    var filteredDataSources = listOf<String>()

    @Nullable
    override fun getState() = this

    override fun loadState(state: LaravelQuerySettings) {
        copyBean(state, this)
    }

    companion object {
        val instance: LaravelQuerySettings
            get() = LaravelQuerySettings(ProjectManager.getInstance().defaultProject)
    }
}
