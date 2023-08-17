package dev.musakavak.uzayan.socket

import dev.musakavak.uzayan.managers.FileManager
import dev.musakavak.uzayan.managers.FileTransferManager
import dev.musakavak.uzayan.managers.ImageThumbnailManager
import dev.musakavak.uzayan.managers.MediaSessionTransferManager
import dev.musakavak.uzayan.managers.NotificationManager
import dev.musakavak.uzayan.services.NLService
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream

class Actions {
    var mediaSessionTransferManager: MediaSessionTransferManager? = null
    var imageThumbnailManager: ImageThumbnailManager? = null
    var fileManager: FileManager? = null
    var fileTransferManager: FileTransferManager? = null
    private var notificationManager: NotificationManager? = null
    var allowNotificationTransfer: Boolean = false
    var allowNotificationControls: Boolean = false
    var allowMediaSessionControls: Boolean = false

    fun mediaSessionControl(json: JSONObject) {
        if (allowMediaSessionControls) {
            mediaSessionTransferManager?.mediaSessionControl(
                json.getJSONObject("input").getString("token"),
                json.getJSONObject("input").getString("action"),
                json.getJSONObject("input").get("value"),
            )
        }
    }

    fun mediaSessionRequest() {
        mediaSessionTransferManager?.sendCurrentSessions()
    }

    fun notificationAction(json: JSONObject) {
        if (allowNotificationControls) NLService.sendAction?.let {
            it(
                json.getJSONObject("input").getString("key"),
                json.getJSONObject("input").getString("action"),
                json.getJSONObject("input").getString("input")
            )
        }
    }

    fun notificationsRequest() {
        if (allowNotificationTransfer) NLService.sendActiveNotifications?.let { it() }
    }

    fun imageThumbnailRequest(json: JSONObject) {
        imageThumbnailManager?.sendSlice(
            json.getJSONObject("input").getInt("start"),
            json.getJSONObject("input").getInt("length"),
        )
    }

    fun fileSystemRequest(json: JSONObject) {
        fileManager?.sendFileSystem(
            json.getJSONObject("input").getString("path"),
        )
    }

    suspend fun sendFile(path: String, input: InputStream, output: OutputStream) {
        fileTransferManager?.sendFile(
            path,
            input,
            output,
        )
    }

    suspend fun receiveFile(path: String, input: InputStream, output: OutputStream) {
        fileTransferManager?.createFile(
            path,
            0,
            input,
            output,
            notificationManager
        )
    }

    fun deleteFileRequest(json: JSONObject) {
        fileManager?.deleteFile(
            json.getJSONObject("input").getString("path"),
        )
    }

    fun moveFileRequest(json: JSONObject) {
        fileManager?.moveFile(
            json.getJSONObject("input").getString("source"),
            json.getJSONObject("input").getString("target"),
        )
    }

}