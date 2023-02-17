package dev.musakavak.uzayan.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dev.musakavak.uzayan.managers.NotificationManager

class NLService : NotificationListenerService() {
    private var notificationManager: NotificationManager? = null

    override fun onListenerConnected() {
        super.onListenerConnected()

        notificationManager = NotificationManager(applicationContext) { this.sendNotifications() }
        println("Notification Lıstener Connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            notificationManager?.sendNotification(sbn)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let {
            notificationManager?.sendRemoveNotification(it.key)
        }
    }

    private fun sendNotifications() {
        notificationManager?.sendNotificationList(activeNotifications)
    }
}