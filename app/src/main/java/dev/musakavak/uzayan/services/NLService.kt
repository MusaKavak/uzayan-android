package dev.musakavak.uzayan.services

import android.media.MediaMetadata
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NLService:NotificationListenerService() {
    private val tag = "NotificationListener"
    override fun onListenerConnected() {
        super.onListenerConnected()

        println("Notification Lıstener Connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {

    }

    fun printBundle(bundle: Bundle?,message:Any) {
        val extras = bundle?.keySet()
        Log.w(tag, "-------------------------------------------------$message")
        extras?.forEach {
            Log.i(tag, "Key: $it ----------- Value: ${bundle.get(it)}")
        }
        Log.w(tag, "-------------------------------------------------")
    }
}