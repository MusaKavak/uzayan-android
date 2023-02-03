package dev.musakavak.uzayan.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import dev.musakavak.uzayan.utils.MediaSessionManager
import dev.musakavak.uzayan.utils.UdpSocket
import kotlinx.coroutines.*

class UzayanForegroundService : Service() {
    private val channelId = "uzayan"
    private val channelName = "Uzayan Foreground"

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)

        val notification = Notification.Builder(this, channelId)
            .build()

        startForeground(34724, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        MediaSessionManager(this).listen()

        CoroutineScope(Dispatchers.IO).launch {
            UdpSocket.initializeSocket()
            CoroutineScope(Dispatchers.IO).launch {
                UdpSocket.listenSocket()
            }
//            while (true) {
//                UdpSocket.sendMessage("Hello From Android", "192.168.1.110")
//                delay(5000L)
//            }
        }
    }
}