package dev.musakavak.uzayan.services

import android.app.RemoteInput
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
        var sendAction: ((String, String, String?) -> Unit)? = null
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
            nf.actions.map {
                if (it.remoteInputs.isNullOrEmpty()) it.title.toString()
                else "*REPLY*${it.title}"
            }
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

    private fun sendAction(key: String, action: String, input: String?) {
        val isReply = action.contains("*REPLY*")
        val actionTitle =
            if (isReply) action.substringAfter("*REPLY*")
            else action

        val actionObject = getActiveNotifications(arrayOf(key)).firstOrNull { it.key == key }
            ?.let { sbn ->
                sbn.notification.actions?.firstOrNull { it.title.toString() == actionTitle }
            }

        actionObject?.let { actionObj ->
            if (!isReply) {
                actionObj.actionIntent?.send()
                return
            }
            if (!input.isNullOrEmpty() && !actionObj.remoteInputs.isNullOrEmpty()) {
                val remoteInputs = mutableListOf<RemoteInput>()

                val localIntent = Intent()
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val localBundle = Bundle()

                actionObj.remoteInputs.forEach {
                    localBundle.putCharSequence(it.resultKey, input)
                    remoteInputs.add(it)
                }

                RemoteInput.addResultsToIntent(
                    remoteInputs.toTypedArray(),
                    localIntent,
                    localBundle
                )

                actionObj.actionIntent?.send(this, 0, localIntent)
            }
        }
    }
}