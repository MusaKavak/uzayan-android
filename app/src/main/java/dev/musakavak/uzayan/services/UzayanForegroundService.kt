package dev.musakavak.uzayan.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import dev.musakavak.uzayan.managers.ImageManager
import dev.musakavak.uzayan.managers.MediaSessionManager
import dev.musakavak.uzayan.socket.Actions
import dev.musakavak.uzayan.socket.Listener
import dev.musakavak.uzayan.socket.TcpSocketServer
import dev.musakavak.uzayan.socket.UdpSocket
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
        val mediaSessionManager = MediaSessionManager(this)
        mediaSessionManager.listen()
        val imageManager = ImageManager(this)
        CoroutineScope(Dispatchers.IO).launch {
            TcpSocketServer(
                Actions(
                    mediaSessionManager,
                    imageManager
                )
            ).initializeSocketServer()
//            val socket = UdpSocket.initializeSocket()
//            CoroutineScope(Dispatchers.IO).launch {
//                socket?.let {
//                    Listener(socket, mediaSessionManager,imageManager).listen()
//                }
//            }
        }
    }
}