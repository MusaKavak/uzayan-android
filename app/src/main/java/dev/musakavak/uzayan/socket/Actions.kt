package dev.musakavak.uzayan.socket

import dev.musakavak.uzayan.managers.ImageManager
import dev.musakavak.uzayan.managers.MediaSessionManager
import dev.musakavak.uzayan.managers.NotificationManager
import org.json.JSONObject

class Actions(
    private val mediaSessionManager: MediaSessionManager,
    private val imageManager: ImageManager
) {

    fun mediaSessionControl(json: JSONObject) {
        mediaSessionManager.mediaSessionControl(
            json.getJSONObject("input").getString("token"),
            json.getJSONObject("input").getString("action"),
            json.getJSONObject("input").get("value"),
        )
    }

    fun mediaSessionRequest(){
        mediaSessionManager.sendCurrentSessions()
    }

    fun notificationAction(json: JSONObject){
        NotificationManager.sendAction(
            json.getJSONObject("input").getString("key"),
            json.getJSONObject("input").getString("action")
        )
    }

    fun notificationsRequest(){
        NotificationManager.syncNotifications()
    }

    fun imageThumbnailRequest(json: JSONObject){
        imageManager.sendSlice(
            json.getJSONObject("input").getInt("start"),
            json.getJSONObject("input").getInt("length"),
        )
    }

    fun fullSizeImageRequest(json: JSONObject){
        imageManager.sendFullSizeImage(
            json.getJSONObject("input").getString("id"),
        )
    }
}