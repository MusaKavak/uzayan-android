package dev.musakavak.uzayan.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dev.musakavak.uzayan.managers.NotificationTransferManager

class NLService : NotificationListenerService() {

    companion object {
        var allowNotificationTransfer: Boolean = false
        var sendActiveNotifications: (() -> Unit) = {}
    }

    private var notificationManager: NotificationTransferManager? = null

    override fun onListenerConnected() {
        super.onListenerConnected()

        notificationManager = NotificationTransferManager(applicationContext)
        sendActiveNotifications = {
            notificationManager?.sendNotificationList(activeNotifications)
        }
        println("Notification Lıstener Connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (allowNotificationTransfer) {
            sbn?.let {
                notificationManager?.sendNotification(sbn)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (allowNotificationTransfer) {
            sbn?.let {
                notificationManager?.sendRemoveNotification(it.key,it.groupKey)
            }
        }
    }

    private fun sendNotifications() {
        notificationManager?.sendNotificationList(activeNotifications)
    }
}