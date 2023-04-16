package dev.musakavak.uzayan.socket

import android.content.SharedPreferences
import dev.musakavak.uzayan.managers.*
import dev.musakavak.uzayan.services.NLService
import org.json.JSONObject
import java.net.Socket

class Actions(
    private val shp: SharedPreferences,
    private val mediaSessionManager: MediaSessionManager,
    private val imageManager: ImageManager,
    private val fileManager: FileManager,
    private val fileTransferManager: FileTransferManager
) {

    fun mediaSessionControl(json: JSONObject) {
        mediaSessionManager.mediaSessionControl(
            json.getJSONObject("input").getString("token"),
            json.getJSONObject("input").getString("action"),
            json.getJSONObject("input").get("value"),
        )
    }

    fun mediaSessionRequest() {
        mediaSessionManager.sendCurrentSessions()
    }

    fun notificationAction(json: JSONObject) {
        NotificationManager.sendAction(
            json.getJSONObject("input").getString("key"),
            json.getJSONObject("input").getString("action")
        )
    }

    fun notificationsRequest() {
        NLService.sendActiveNotifications()
    }

    fun imageThumbnailRequest(json: JSONObject) {
        imageManager.sendSlice(
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
        imageManager.sendFullSizeImage(
            json.getJSONObject("input").getString("id"),
        )
    }

    suspend fun createFile(json: JSONObject, socket: Socket) {
        fileTransferManager.createFile(
            json.getString("path"),
            json.getLong("size"),
            socket
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