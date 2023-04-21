package dev.musakavak.uzayan.socket

import android.content.SharedPreferences
import dev.musakavak.uzayan.managers.FileManager
import dev.musakavak.uzayan.managers.FileTransferManager
import dev.musakavak.uzayan.managers.ImageTransferManager
import dev.musakavak.uzayan.managers.MediaSessionTransferManager
import dev.musakavak.uzayan.managers.NotificationTransferManager
import dev.musakavak.uzayan.services.NLService
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream

class imageManagerActions(
    private val shp: SharedPreferences,
    private val mediaSessionTransferManager: MediaSessionTransferManager,
    private val imageTransferManager: ImageTransferManager,
    private val fileManager: FileManager,
    private val fileTransferManager: FileTransferManager
) {

    fun mediaSessionControl(json: JSONObject) {
        mediaSessionTransferManager.mediaSessionControl(
            json.getJSONObject("input").getString("token"),
            json.getJSONObject("input").getString("action"),
            json.getJSONObject("input").get("value"),
        )
    }

    fun mediaSessionRequest() {
        mediaSessionTransferManager.sendCurrentSessions()
    }

    fun notificationAction(json: JSONObject) {
        NotificationTransferManager.sendAction(
            json.getJSONObject("input").getString("key"),
            json.getJSONObject("input").getString("action")
        )
    }

    fun notificationsRequest() {
        NLService.sendActiveNotifications()
    }

    fun imageThumbnailRequest(json: JSONObject) {
        imageTransferManager.sendSlice(
            json.getJSONObject("input").getInt("start"),
            json.getJSONObject("input").getInt("length"),
        )
    }

    fun fileSystemRequest(json: JSONObject) {
        fileManager.sendFileSystem(
            json.getJSONObject("input").getString("path"),
        )
    }

    fun fileRequest(json: JSONObject) {
        fileManager.sendFile(
            json.getJSONObject("input").getString("path"),
        )
    }

    fun deleteFileRequest(json: JSONObject) {
        fileManager.deleteFile(
            json.getJSONObject("input").getString("path"),
        )
    }

    fun moveFileRequest(json: JSONObject) {
        fileManager.moveFile(
            json.getJSONObject("input").getString("source"),
            json.getJSONObject("input").getString("target"),
        )
    }

    fun fullSizeImageRequest(json: JSONObject) {
        imageTransferManager.sendFullSizeImage(
            json.getJSONObject("input").getString("id"),
        )
    }

    suspend fun createFile(json: JSONObject, input: InputStream, output: OutputStream) {
        fileTransferManager.createFile(
            json.getString("path"),
            json.getLong("size"),
            input,
            output
        )
    }

    fun pair(json: JSONObject) {
        val address = json.getJSONObject("input").getString("address")
        val port = json.getJSONObject("input").getString("port")

        shp.edit().apply {
            putString("ClientAddress", address)
            putString("ClientPort", port)
            apply()
        }
    }
}