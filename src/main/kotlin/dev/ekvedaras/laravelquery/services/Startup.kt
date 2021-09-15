package dev.ekvedaras.laravelquery.services

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import dev.ekvedaras.laravelquery.notifications.ConfigureSettingsNotification

class Startup : StartupActivity {
    override fun runActivity(project: Project) {
        val settings = LaravelQuerySettings.getInstance(project)

        if (!settings.filterDataSources && !settings.ignoreSettings) {
            ConfigureSettingsNotification().notify(
                project,
                "Laravel query now allows you to configure which schemas to inspect and reduce the noise!"
            )
        }
    }
}
