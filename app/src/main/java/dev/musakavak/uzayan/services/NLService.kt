package dev.musakavak.uzayan.services

import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dev.musakavak.uzayan.models.Notification
import dev.musakavak.uzayan.socket.Emitter
import dev.musakavak.uzayan.tools.Base64Tool
import android.app.Notification as AndroidNotification

class NLService : NotificationListenerService() {
    private val base64Tool = Base64Tool()

    companion object {
        var allowNotificationTransfer: Boolean = false
        var sendActiveNotifications: (() -> Unit)? = null
        var sendAction: ((String, String) -> Unit)? = null
    }


    override fun onListenerConnected() {
        super.onListenerConnected()
        println("Notification Lıstener Connected")
        sendActiveNotifications = ::sendActiveNotifications
        sendAction = ::sendAction
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (allowNotificationTransfer) {
            sbn?.let {
                if (sbn.notification.extras.containsKey("android.mediaSession")) return
                Emitter.emit("Notification", createNotification(it))
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun createNotification(sbn: StatusBarNotification): Notification {
        val nf = sbn.notification
        val extras = nf.extras

        return Notification(
            sbn.key,
            sbn.groupKey,
            getStringFromExtras(extras, "android.title"),
            getStringFromExtras(extras, "android.text"),
            getStringFromExtras(extras, "android.bigText"),
            getStringFromExtras(extras, "android.infoText"),
            base64Tool.fromIcon(nf.getLargeIcon(), this),
            getNotificationActions(nf),
            extras.get("android.progressMax") as Int?,
            extras.get("android.progress") as Int?,
        )
    }

    private fun getNotificationActions(nf: AndroidNotification): List<String>? {
        return if (!nf.actions.isNullOrEmpty())
            nf.actions.map { it.title.toString() }
        else null
    }

    private fun getStringFromExtras(extras: Bundle, key: String): String? {
        extras.getCharSequence(key)?.let {
            if (it.isNotBlank()) return it.toString()
        }
        return null
    }


    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (allowNotificationTransfer) {
            sbn?.let {
                Emitter.emit("RemoveNotification", Notification(it.key, it.groupKey))
            }
        }
    }

    private fun sendActiveNotifications() {
        val notifications = mutableListOf<Notification>()
        activeNotifications.forEach {
            if (!it.notification.extras.containsKey("android.mediaSession"))
                notifications.add(createNotification(it))
        }
        Emitter.emit("Notifications", notifications)
    }

    private fun sendAction(key: String, action: String) {
        getActiveNotifications(arrayOf(key)).firstOrNull { it.key == key }?.let { sbn ->
            sbn.notification.actions?.firstOrNull { it.title.toString() == action }
                ?.actionIntent
                ?.send(this, 0, Intent())
        }
    }
}