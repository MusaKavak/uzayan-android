package dev.musakavak.uzayan.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.MediaSessionManager
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

class UzayanForegroundService : Service() {
    private val channelId = "uzayan"
    private val channelName = "Uzayan Foreground"
    private val actions = Actions()

    companion object {
        var setActionAllowList: (allowList: AllowList) -> Unit = {}
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

        setActionAllowList = { setActionAllowList(it) }
        startForeground(34724, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        CoroutineScope(Dispatchers.IO).launch { SocketServer(actions).initialize() }
    }

    private fun setActionAllowList(allowList: AllowList) {
        setNotificationTransferAllowance(allowList)
        setMediaSessionAllowance(allowList)
        setFileAllowance(allowList)
    }

    private fun setFileAllowance(allowList: AllowList) {
        if (allowList.file) {
            if (actions.fileManager == null) actions.fileManager = FileManager()
            if (actions.fileTransferManager == null) actions.fileTransferManager =
                FileTransferManager()
            if (actions.imageTransferManager == null) actions.imageTransferManager =
                ImageTransferManager(applicationContext.contentResolver)
        } else {
            actions.fileManager = null
            actions.fileTransferManager = null
        }
    }

    private fun setMediaSessionAllowance(allowList: AllowList) {
        if (allowList.mediaSession) {
            if (actions.mediaSessionTransferManager == null)
                actions.mediaSessionTransferManager = getMSTManager()
            actions.allowMediaSessionControls = allowList.mediaSessionControl
        } else {
            actions.mediaSessionTransferManager = null
            actions.allowMediaSessionControls = false
        }
    }

    private fun setNotificationTransferAllowance(allowList: AllowList) {
        NLService.allowNotificationTransfer = allowList.notifications
        actions.allowNotificationTransfer = allowList.notifications
        actions.allowNotificationControls =
            allowList.notifications && allowList.notificationControls
    }

    private fun getMSTManager(): MediaSessionTransferManager {
        return MediaSessionTransferManager(
            applicationContext.getSystemService(MediaSessionManager::class.java),
            ComponentName(applicationContext, NLService::class.java)
        ).also { it.listen() }
    }
}