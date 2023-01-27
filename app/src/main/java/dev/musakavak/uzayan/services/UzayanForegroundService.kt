package dev.musakavak.uzayan.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import dev.musakavak.uzayan.MainActivity
import dev.musakavak.uzayan.utils.UdpSocket
import java.net.DatagramSocket
import java.net.InetAddress

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
        UdpSocket.initializeSocket()
        while (true){
            UdpSocket.sendMessage("Hello From Android","192.168.1.110")
            Thread.sleep(10000)
        }
    }
}