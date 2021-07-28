package dev.ekvedaras.laravelquery.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean

@State(name = "LaravelQuerySettings", storages = [Storage("laravel-query-settings.xml")])
class LaravelQuerySettings : PersistentStateComponent<LaravelQuerySettings> {
    var filterDataSources = false
    var filteredDataSources = setOf<String>()

    override fun getState() = this

    override fun loadState(state: LaravelQuerySettings) {
        copyBean(state, this)
    }

    companion object {
        val instance: LaravelQuerySettings
            get() = ApplicationManager.getApplication().getService(LaravelQuerySettings::class.java)
    }
}
