package dev.musakavak.uzayan.managers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationManager(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    private val nfFileId = "uznfile"

    private var nfId = 0
    private fun getNfId(): Int {
        nfId++
        return nfId
    }

    init {
        val fileTransferChanel =
            NotificationChannel(nfFileId, "File Transfer", NotificationManager.IMPORTANCE_LOW)

        notificationManager.createNotificationChannel(fileTransferChanel)

        val c = createFileTransferNotification("Receiving TestFile", "TestFile Received")

        CoroutineScope(Dispatchers.Default).launch {
            var progress = 0
            while (progress <= 100) {
                c(progress)
                progress++
                delay(200L)
            }
        }
    }

    fun createFileTransferNotification(title: String, endTitle: String): (progress: Int) -> Unit {
        val id = getNfId()
        val builder = NotificationCompat.Builder(context, nfFileId)
            .setContentTitle(title)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, 0, false)
            .setUsesChronometer(true)
            .setGroup("TransferringFiles")

        notificationManager.notify(id, builder.build())

        return {
            if (it == 100) {
                builder
                    .setProgress(0, 0, false)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setOngoing(false)
                    .setContentTitle(endTitle)
                    .setUsesChronometer(false)
                    .setGroup("TransferredFiles")
                createTransferredFilesGroupSummary()
            } else builder
                .setProgress(100, it, false)
            notificationManager.notify(id, builder.build())
        }
    }

    private fun createTransferredFilesGroupSummary() {
        val sum = NotificationCompat.Builder(context, nfFileId)
            .setSmallIcon(android.R.drawable.ic_menu_share)
            .setContentTitle("Files Transferred")
            .setGroup("TransferredFiles")
            .setGroupSummary(true)
            .build()
        notificationManager.notify(1199, sum)
    }
}