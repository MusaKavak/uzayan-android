package dev.musakavak.uzayan.managers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.notification.StatusBarNotification
import android.util.Log
import dev.musakavak.uzayan.models.Notification
import dev.musakavak.uzayan.socket.TcpSocket
import dev.musakavak.uzayan.tools.Base64Tool
import android.app.Notification as AndroidNotification

class NotificationManager(private val context: Context) {
    private val base64Tool = Base64Tool()

    companion object {
        private val currentNotificationActions = mutableListOf<NotificationAction>()
        fun sendAction(key: String, actionTitle: String) {
            currentNotificationActions
                .firstOrNull { it.notificationKey == key }
                ?.actions
                ?.firstOrNull { it.actionTitle == actionTitle }
                ?.invoke
                ?.let { it() }
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
        TcpSocket.emit("Notification", createNotification(sbn))
    }

    fun sendNotificationList(list: Array<StatusBarNotification>) {
        val notifications = mutableListOf<Notification>()
        list.forEach {
            if (!it.notification.extras.containsKey("android.mediaSession"))
                notifications.add(createNotification(it))
        }
        TcpSocket.emit("Notifications", notifications)
    }

    fun sendRemoveNotification(key: String) {
        TcpSocket.emit("RemoveNotification", key)
    }

    @Suppress("DEPRECATION")
    private fun createNotification(sbn: StatusBarNotification): Notification {
        val nf = sbn.notification
        val extras = nf.extras
        return Notification(
            extras.getString("android.infoText"),
            sbn.key,
            base64Tool.fromIcon(nf.getLargeIcon(), context),
            sbn.packageName,
            extras.getCharSequence("android.title").toString(),
            extras.getCharSequence("android.text").toString(),
            extras.getCharSequence("android.bigText").toString(),
            createNotificationActions(nf, sbn.key),
            extras.get("android.progressMax") as Int?,
            extras.get("android.progress") as Int?,
            sbn.isGroup
        )
    }

    private fun createNotificationActions(nf: AndroidNotification, key: String): List<String>? {
        if (nf.actions != null && nf.actions.isNotEmpty()) {
            val actions = mutableListOf<Action>()
            val actionTitles = mutableListOf<String>()
            nf.actions.forEach {
                val title = it.title.toString()
                actions.add(Action(title) {
                    it.actionIntent.send(context, 0, Intent())
                })
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

    private fun printBundle(b: Bundle?) {
        b?.let {
            val s = b.keySet()
            Log.e("Tagg", "---------------------------------")
            s.forEach {
                Log.i(
                    "Tagg",
                    "Key: $it, Value: ${b.get(it)}, Type: ${b.get(it)?.javaClass?.canonicalName}"
                )
            }
        }
    }
}