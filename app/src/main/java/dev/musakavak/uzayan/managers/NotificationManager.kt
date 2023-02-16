package dev.musakavak.uzayan.managers

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Icon
import android.service.notification.StatusBarNotification
import android.util.Log
import dev.musakavak.uzayan.models.Notification
import dev.musakavak.uzayan.socket.Emitter
import dev.musakavak.uzayan.tools.Base64Tool

class NotificationManager(private val context: Context) {
    private val base64Tool = Base64Tool()

    fun sendNotification(sbn: StatusBarNotification) {
        if (sbn.notification.extras.containsKey("android.mediaSession")) return
        Emitter.emitNotification(createNotification(sbn))
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
            extras.getString("android.text")
        )
    }

}