package dev.ekvedaras.laravelquery.services

import com.intellij.database.model.DasNamespace
import com.intellij.database.model.DasTable
import com.intellij.database.psi.DbDataSource
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import dev.ekvedaras.laravelquery.models.SettingsSchema
import org.jetbrains.annotations.Nullable

@State(name = "LaravelQuerySettings", storages = [Storage("laravel-query-settings.xml")])
class LaravelQuerySettings : PersistentStateComponent<LaravelQuerySettings> {
    var filterDataSources = false
    var filteredDataSources = setOf<String>()
    var ignoreSettings = false
    var tablePrefix = ""

    @Nullable
    override fun getState() = this

    override fun loadState(state: LaravelQuerySettings) {
        copyBean(state, this)
    }

    companion object {
        fun getInstance(project: Project): LaravelQuerySettings {
            return project.service()
        }
    }

    fun interestedIn(dataSource: DbDataSource) =
        !filterDataSources || filteredDataSources.any { it.startsWith(SettingsSchema.keyFor("", dataSource.uniqueId)) }

    fun interestedIn(namespace: DasNamespace, dataSource: DbDataSource) =
        !filterDataSources || filteredDataSources.contains(SettingsSchema.keyFor(namespace, dataSource))

    fun interestedIn(table: DasTable, dataSource: DbDataSource) =
        interestedIn(table.dasParent as DasNamespace, dataSource)
}
