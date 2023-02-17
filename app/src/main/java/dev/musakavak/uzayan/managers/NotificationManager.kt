package dev.musakavak.uzayan.managers

import android.app.Notification as AndroidNotification
import android.content.Context
import android.service.notification.StatusBarNotification
import dev.musakavak.uzayan.models.Notification
import dev.musakavak.uzayan.socket.Emitter
import dev.musakavak.uzayan.tools.Base64Tool

class NotificationManager(
    private val context: Context,
    private val _getCurrentNotifications: () -> Unit
) {
    private val base64Tool = Base64Tool()

    init {
        getCurrentNotifications = _getCurrentNotifications
    }

    companion object {
        private val currentNotificationActions = mutableListOf<NotificationAction>()
        private var getCurrentNotifications: (() -> Unit)? = null

        fun sendAction(key: String, actionTitle: String) {
            currentNotificationActions
                .firstOrNull { it.notificationKey == key }
                ?.actions
                ?.firstOrNull { it.actionTitle == actionTitle }
                ?.invoke
                ?.let { it() }
        }

        fun syncNotifications() {
            getCurrentNotifications?.let { it() }
        }
    }

    private data class Action(
        val actionTitle: String?,
        val invoke: () -> Unit,
    )

    private data class NotificationAction(
        val notificationKey: String,
        val actions: List<Action>
    )


    fun sendNotification(sbn: StatusBarNotification) {
        if (sbn.notification.extras.containsKey("android.mediaSession")) return
        Emitter.emitNotification(createNotification(sbn))
    }

    fun sendNotificationList(list: Array<StatusBarNotification>) {
        val notifications = mutableListOf<Notification>()
        list.forEach {
            if (!it.notification.extras.containsKey("android.mediaSession"))
                notifications.add(createNotification(it))
        }
        Emitter.emitNotifications(notifications)
    }

    fun sendRemoveNotification(key: String) {
        Emitter.emitRemoveNotification(key)
    }

    private fun createNotification(sbn: StatusBarNotification): Notification {
        val nf = sbn.notification
        val extras = nf.extras
        return Notification(
            extras.getString("android.infoText"),
            sbn.key,
            base64Tool.fromIcon(nf.getLargeIcon(), context),
            sbn.packageName,
            extras.getString("android.title"),
            extras.getString("android.text"),
            createNotificationActions(nf, sbn.key)
        )
    }

    private fun createNotificationActions(nf: AndroidNotification, key: String): List<String>? {
        if (nf.actions != null && nf.actions.isNotEmpty()) {
            val actions = mutableListOf<Action>()
            val actionTitles = mutableListOf<String>()
            nf.actions.forEach {
                val title = it.title.toString()
                actions.add(Action(title) { it.actionIntent.send() })
                actionTitles.add(title)
            }
            val na = NotificationAction(key, actions)
            val found = currentNotificationActions.indexOfFirst { it.notificationKey == key }
            if (found == -1) currentNotificationActions.add(na)
            else currentNotificationActions[found] = na
            return actionTitles
        }
        return null
    }
}