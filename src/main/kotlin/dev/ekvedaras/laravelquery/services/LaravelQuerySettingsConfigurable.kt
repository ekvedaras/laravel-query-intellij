package dev.ekvedaras.laravelquery.services

import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.services.forms.LaravelQuerySettingsForm
import javax.swing.JComponent
import org.jetbrains.annotations.Nls

class LaravelQuerySettingsConfigurable(val project: Project) : SearchableConfigurable {
    var settingsForm: LaravelQuerySettingsForm? = null

    override fun createComponent(): JComponent? {
        settingsForm = settingsForm ?: LaravelQuerySettingsForm(project)
        return settingsForm?.component()
    }

    override fun isModified(): Boolean = settingsForm?.isModified ?: false

    @Throws(ConfigurationException::class)
    override fun apply() {
        val settings = LaravelQuerySettings.getInstance(project)
        settings.filterDataSources = settingsForm?.shouldFilterDataSources() ?: false
        settings.filteredDataSources = settingsForm?.filteredDataSources() ?: setOf()
    }

    override fun reset() {
        settingsForm?.loadSettings()
    }

    override fun disposeUIResources() {
        settingsForm = null
    }

    @Nls
    override fun getDisplayName() = "Laravel Query"

    override fun getId(): String = ID

    companion object {
        const val ID = "preferences.ekvedaras.laravelquery"
    }
}
