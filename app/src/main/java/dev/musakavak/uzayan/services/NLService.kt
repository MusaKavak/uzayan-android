package dev.musakavak.uzayan.services

import android.media.MediaMetadata
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import dev.musakavak.uzayan.managers.NotificationManager

class NLService : NotificationListenerService() {
    private val tag = "NotificationListener"
    private var notificationManager: NotificationManager? = null

    override fun onListenerConnected() {
        super.onListenerConnected()

        notificationManager = NotificationManager(applicationContext)
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

    fun printBundle(bundle: Bundle?, message: Any) {
        val extras = bundle?.keySet()
        Log.w(tag, "-------------------------------------------------$message")
        extras?.forEach {
            val value = bundle.get(it)
            Log.i(
                tag,
                "Key: $it ----------- Value: ${value}---------------------${if (value != null) value::class.qualifiedName else ""}"
            )
        }
        Log.w(tag, "-------------------------------------------------")
    }
}
//        sbn?.let {
//            printBundle(sbn.notification.extras, "${sbn.packageName} ${sbn.key} ${sbn.groupKey}")
//            println("****************************************************************************************************************************************************************************")
//            sbn.notification.actions?.forEach { action ->
//                sbn.
//                action?.let {
//                    printBundle(action.extras, "Title: ${action.title}")
//                }
//            }
//        }
