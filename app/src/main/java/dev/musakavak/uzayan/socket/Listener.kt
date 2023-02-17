package dev.musakavak.uzayan.socket

import com.google.gson.Gson
import dev.musakavak.uzayan.managers.MediaSessionManager
import dev.musakavak.uzayan.managers.NotificationManager
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket

class Listener(
    private val socket: DatagramSocket,
    private val mediaSessionManager: MediaSessionManager
) {
    private val gson = Gson()

    fun listen() {
        val packet = DatagramPacket(
            ByteArray(30000),
            30000,
        )
        socket.receive(packet)
        call(packet)
        listen()
    }

    private fun call(packet: DatagramPacket) {
        val string = String(packet.data)
        val json = JSONObject(string)
        println("Received Packet With Message:  " + json.get("message"))
        when (json.get("message")) {
            "MediaSessionControl" -> {
                mediaSessionManager.mediaSessionControl(
                    json.getJSONObject("input").getString("token"),
                    json.getJSONObject("input").getString("action")
                )
            }
            "MediaSessionsRequest" -> mediaSessionManager.sendCurrentSessions()
            "NotificationAction" -> {
                NotificationManager.sendAction(
                    json.getJSONObject("input").getString("key"),
                    json.getJSONObject("input").getString("action")
                )
            }
            "NotificationsRequest" -> NotificationManager.syncNotifications()
            else -> {
                println("Message Not Found")
            }
        }
    }
}