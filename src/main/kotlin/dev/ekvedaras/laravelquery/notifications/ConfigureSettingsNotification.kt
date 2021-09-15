package dev.ekvedaras.laravelquery.notifications

import com.intellij.ide.SaveAndSyncHandler
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import dev.ekvedaras.laravelquery.services.LaravelQuerySettings
import dev.ekvedaras.laravelquery.services.LaravelQuerySettingsConfigurable

class ConfigureSettingsNotification {
    private val group = NotificationGroupManager.getInstance()
        .getNotificationGroup("Laravel Query Settings")

    fun notify(project: Project, content: String): Notification {
        val settings = LaravelQuerySettings.getInstance(project)
        val notification: Notification = group.createNotification(content, NotificationType.INFORMATION)

        notification.addActions(
            mutableListOf(
                object : NotificationAction("Configure") {
                    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                        ShowSettingsUtil
                            .getInstance()
                            .editConfigurable(project, LaravelQuerySettingsConfigurable(project))
                        notification.hideBalloon()
                    }
                },
                object : NotificationAction("Ignore") {
                    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                        settings.ignoreSettings = true
                        SaveAndSyncHandler
                            .getInstance()
                            .scheduleSave(SaveAndSyncHandler.SaveTask(project, true))
                        notification.hideBalloon()
                    }
                },
            )
        )

        notification.notify(project)
        return notification
    }
}
