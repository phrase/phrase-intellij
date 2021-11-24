package com.phrase.intellij

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project

object Notify {
    fun error(content: String, project: Project? = null, action: AnAction? = null) {
        val notification = Notification(
            "Phrase Notifications",
            content,
            NotificationType.ERROR
        )
        if(action!=null) notification.addAction(action)
        Notifications.Bus.notify(notification, project)
    }
}