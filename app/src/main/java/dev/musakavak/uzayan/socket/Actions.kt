package dev.musakavak.uzayan.socket

import dev.musakavak.uzayan.managers.FileManager
import dev.musakavak.uzayan.managers.FileTransferManager
import dev.musakavak.uzayan.managers.ImageTransferManager
import dev.musakavak.uzayan.managers.MediaSessionTransferManager
import dev.musakavak.uzayan.managers.NotificationManager
import dev.musakavak.uzayan.managers.NotificationTransferManager
import dev.musakavak.uzayan.services.NLService
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream

class Actions {
    var mediaSessionTransferManager: MediaSessionTransferManager? = null
    var imageTransferManager: ImageTransferManager? = null
    var fileManager: FileManager? = null
    var fileTransferManager: FileTransferManager? = null
    var notificationManager: NotificationManager? = null
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
        if (allowNotificationControls) NotificationTransferManager.sendAction(
            json.getJSONObject("input").getString("key"),
            json.getJSONObject("input").getString("action")
        )
    }

    fun notificationsRequest() {
        if (allowNotificationTransfer) NLService.sendActiveNotifications()
    }

    fun imageThumbnailRequest(json: JSONObject) {
        imageTransferManager?.sendSlice(
            json.getJSONObject("input").getInt("start"),
            json.getJSONObject("input").getInt("length"),
        )
    }

    fun fileSystemRequest(json: JSONObject) {
        fileManager?.sendFileSystem(
            json.getJSONObject("input").getString("path"),
        )
    }

    suspend fun fileRequest(json: JSONObject, input: InputStream, output: OutputStream) {
        val id = json.getString("id")
        val size = json.getString("size").toLongOrNull()
        val fileInput: InputStream? = when (json.getString("transferType")) {
            "FileTransfer" -> fileManager?.getFileToSend(id)
            "ImageTransfer" -> imageTransferManager?.getImageInputStream(id)
            else -> null
        }

        fileTransferManager?.sendFromInputStream(
            fileInput,
            size,
            input,
            output,
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

    suspend fun createFileRequest(json: JSONObject, input: InputStream, output: OutputStream) {
        fileTransferManager?.createFile(
            json.getString("path"),
            json.getLong("size"),
            input,
            output,
            notificationManager
        )
    }
}