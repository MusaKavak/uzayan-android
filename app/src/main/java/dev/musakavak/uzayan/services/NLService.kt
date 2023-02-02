package dev.musakavak.uzayan.services

import android.service.notification.NotificationListenerService

class NLService:NotificationListenerService() {
    override fun onListenerConnected() {
        super.onListenerConnected()

        println("Notification Lıstener Connected")
    }
}