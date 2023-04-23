package dev.musakavak.uzayan.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import dev.musakavak.uzayan.managers.FileManager
import dev.musakavak.uzayan.managers.FileTransferManager
import dev.musakavak.uzayan.managers.ImageTransferManager
import dev.musakavak.uzayan.managers.MediaSessionTransferManager
import dev.musakavak.uzayan.socket.Actions
import dev.musakavak.uzayan.socket.TcpSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.musakavak.uzayan.managers.NotificationManager as UznNotificationManager

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
        val shp = getSharedPreferences("UzayanConnection", MODE_PRIVATE)
        val notificationManager = UznNotificationManager(applicationContext)
        val mediaSessionTransferManager = MediaSessionTransferManager(this)
        val fileManager = FileManager()
        val fileTransferManager = FileTransferManager()
        mediaSessionTransferManager.listen()
        val imageTransferManager = ImageTransferManager(this)
        CoroutineScope(Dispatchers.IO).launch {
            TcpSocket(
                Actions(
                    shp,
                    mediaSessionTransferManager,
                    imageTransferManager,
                    fileManager,
                    fileTransferManager,
                    notificationManager
                )
            ).initializeSocketServer()
        }
    }
}