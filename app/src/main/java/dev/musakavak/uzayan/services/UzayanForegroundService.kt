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
import dev.musakavak.uzayan.models.AllowList
import dev.musakavak.uzayan.socket.Actions
import dev.musakavak.uzayan.socket.SocketServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.musakavak.uzayan.managers.NotificationManager as UznNotificationManager

class UzayanForegroundService : Service() {
    private val channelId = "uzayan"
    private val channelName = "Uzayan Foreground"
    private val actions = Actions()

    companion object {
        var setActions: (allowList: AllowList) -> Unit = {}
    }

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

        setActions = { setActions(it) }
        startForeground(34724, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        CoroutineScope(Dispatchers.IO).launch { SocketServer(actions).initialize() }
    }

    private fun setActions(allowList: AllowList) {
        actions.mediaSessionTransferManager =
            if (allowList.mediaSessions) MediaSessionTransferManager(applicationContext).also { it.listen() }
            else null
        actions.notificationManager =
            if (allowList.notifications) UznNotificationManager(applicationContext)
            else null
        if (allowList.fileTransfer) {
            actions.fileManager = FileManager()
            actions.fileTransferManager = FileTransferManager()
            if (allowList.imageTransfer) actions.imageTransferManager =
                ImageTransferManager(applicationContext)
        } else {
            actions.fileManager = null
            actions.fileTransferManager = null
            actions.imageTransferManager = null
        }
        actions.allowNotificationTransfer = allowList.notificationTransfer
    }
}