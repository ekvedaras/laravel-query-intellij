package dev.ekvedaras.laravelquery.services

import com.intellij.openapi.options.SearchableConfigurable
import dev.ekvedaras.laravelquery.services.forms.LaravelQuerySettingsForm
import javax.swing.JComponent

class LaravelQuerySettingsConfigurable : SearchableConfigurable {
    var settingsForm : LaravelQuerySettingsForm? = null

    override fun createComponent(): JComponent? {
        settingsForm = settingsForm ?: LaravelQuerySettingsForm()
        return settingsForm?.component()
    }

    override fun isModified(): Boolean {
        return settingsForm?.isModified ?: false
    }

    override fun apply() {
        val settings = LaravelQuerySettings.instance
        settings.filterDataSources = settingsForm?.shouldFilterDataSources() ?: false
    }

    override fun reset() {
        settingsForm?.loadSettings()
    }

    override fun disposeUIResources() {
        settingsForm = null
    }

    override fun getDisplayName() = "Laravel Query"

    override fun getId(): String = ID

    companion object {
        val ID = "preferences.ekvedaras.laravel-query"
    }
}
