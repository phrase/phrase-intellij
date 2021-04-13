package com.phrase.intellij

import com.intellij.notification.*
import com.intellij.openapi.project.Project
import javax.swing.event.HyperlinkEvent

private val NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Phrase")

object Notify {
    fun error(content: String, project: Project? = null, onLinkEvent:((HyperlinkEvent)->Unit)? = null) {
        val notification = NOTIFICATION_GROUP.createNotification("Phrase", content, NotificationType.ERROR, object:NotificationListener.Adapter() {
            override fun hyperlinkActivated(notification: Notification, e: HyperlinkEvent) {
                onLinkEvent?.invoke(e)
            }
        })
        notification.notify(project)
    }
}