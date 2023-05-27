package dev.musakavak.uzayan.managers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.notification.StatusBarNotification
import dev.musakavak.uzayan.models.Notification
import dev.musakavak.uzayan.socket.Emitter
import dev.musakavak.uzayan.tools.Base64Tool
import android.app.Notification as AndroidNotification

class NotificationTransferManager(private val context: Context) {
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
        Emitter.emit("Notification", createNotification(sbn))
    }

    fun sendNotificationList(list: Array<StatusBarNotification>) {
        val notifications = mutableListOf<Notification>()
        list.forEach {
            if (!it.notification.extras.containsKey("android.mediaSession"))
                notifications.add(createNotification(it))
        }
        Emitter.emit("Notifications", notifications)
    }

    fun sendRemoveNotification(key: String) {
        Emitter.emit("RemoveNotification", key)
    }

    @Suppress("DEPRECATION")
    private fun createNotification(sbn: StatusBarNotification): Notification {
        val nf = sbn.notification
        val extras = nf.extras
        return Notification(
            sbn.key,
            sbn.packageName,
            getStringFromExtras(extras, "android.title"),
            getStringFromExtras(extras, "android.text"),
            getStringFromExtras(extras, "android.bigText"),
            getStringFromExtras(extras, "android.infoText"),
            base64Tool.fromIcon(nf.getLargeIcon(), context),
            base64Tool.fromIcon(nf.smallIcon, context),
            createNotificationActions(nf, sbn.key),
            sbn.isGroup,
            sbn.groupKey,
            extras.get("android.progressMax") as Int?,
            extras.get("android.progress") as Int?,
        )
    }

    private fun getStringFromExtras(extras: Bundle, key: String): String? {
        extras.getCharSequence(key)?.let {
            if (it.isNotBlank()) return it.toString()
        }
        return null
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

//    private fun printBundle(b: Bundle?) {
//        b?.let {
//            val s = b.keySet()
//            Log.e("Tag", "---------------------------------")
//            s.forEach {
//                Log.i(
//                    "Tag",
//                    "Key: $it, Value: ${b.get(it)}, Type: ${b.get(it)?.javaClass?.canonicalName}"
//                )
//            }
//        }
//    }
}