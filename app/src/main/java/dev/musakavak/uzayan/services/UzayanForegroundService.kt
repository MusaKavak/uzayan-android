package dev.musakavak.uzayan.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.MediaSessionManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.managers.AllowListManager
import dev.musakavak.uzayan.managers.FileManager
import dev.musakavak.uzayan.managers.FileTransferManager
import dev.musakavak.uzayan.managers.ImageThumbnailManager
import dev.musakavak.uzayan.managers.MediaSessionTransferManager
import dev.musakavak.uzayan.models.AllowList
import dev.musakavak.uzayan.socket.Actions
import dev.musakavak.uzayan.socket.ConnectionState
import dev.musakavak.uzayan.socket.Server
import dev.musakavak.uzayan.socket.sendPairRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UzayanForegroundService : Service() {
    private val channelId = "uzayan"
    private val channelName = "Uzayan Foreground"
    private val actions = Actions()
    private var server: Server? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        var setActionAllowList: (allowList: AllowList) -> Unit = {}
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
        start(intent?.extras)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(extras: Bundle?) {
        if (extras == null) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }

        val ip = extras.getString("u-ip")
        val port = extras.getInt("u-port")
        val code = extras.getInt("u-code")
        val secure = extras.getBoolean("u-secure")

        if (ip != null && port != 0 && code != 0) {
            initActionAllowlist()
            scope.launch {
                server = Server(actions)
                ConnectionState.currentStatus = 201
                ConnectionState.connectingStatus = R.string.cs_creating
                ConnectionState.isConnectionSecure = secure
                withContext(Dispatchers.Default) {
                    server!!.initialize(secure)
                    Log.i(channelName, "Server running on port: ${server!!.port}")
                }
                server!!.listen()
                ConnectionState.connectingStatus = R.string.cs_sending_request
                try {
                    sendPairRequest(ip, server!!.port!!, port, code, deviceName())
                } catch (e: Exception) {
                    Log.e("PairException", e.message, e)
                }
                ConnectionState.connectingStatus = R.string.cs_waiting_client
            }
        }
    }

    private fun initActionAllowlist() {
        val sp = getSharedPreferences("uzayan_allow_list", Context.MODE_PRIVATE)
        setActionAllowList(AllowListManager(sp).getAllowList())
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
            if (actions.imageThumbnailManager == null) actions.imageThumbnailManager =
                ImageThumbnailManager(applicationContext.contentResolver)
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

    private fun deviceName(): String {
        return Settings.Global.getString(contentResolver, Settings.Global.DEVICE_NAME)
    }

    override fun onDestroy() {
        server?.close()
        scope.cancel()
        Log.i(channelName, "Destroyed")
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}