package dev.ekvedaras.laravelquery.services

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider
import com.intellij.openapi.project.Project

class LaravelQuerySettingsConfigurableProvider(val project: Project) : ConfigurableProvider() {
    override fun createConfigurable(): Configurable? {
        return LaravelQuerySettingsConfigurable(project)
    }
}
