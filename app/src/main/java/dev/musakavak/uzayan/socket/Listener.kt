package dev.musakavak.uzayan.socket

import dev.musakavak.uzayan.managers.ImageManager
import dev.musakavak.uzayan.managers.MediaSessionManager
import dev.musakavak.uzayan.managers.NotificationManager
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket

class Listener(
    private val socket: DatagramSocket,
    private val mediaSessionManager: MediaSessionManager,
    private val imageManager: ImageManager
) {
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
            "TestConnection" -> UdpSocket.emit("TestConnection", null)
            "Pair" -> UdpSocket.setAddress(json.getString("address"))
            "MediaSessionControl" -> mediaSessionManager.mediaSessionControl(
                json.getJSONObject("input").getString("token"),
                json.getJSONObject("input").getString("action"),
                json.getJSONObject("input").get("value"),
            )
            "MediaSessionsRequest" -> mediaSessionManager.sendCurrentSessions()
            "NotificationAction" -> NotificationManager.sendAction(
                json.getJSONObject("input").getString("key"),
                json.getJSONObject("input").getString("action")
            )
            "NotificationsRequest" -> NotificationManager.syncNotifications()
            "ReducedImageRequest" -> imageManager.sendSlice(
                json.getJSONObject("input").getInt("start"),
                json.getJSONObject("input").getInt("length"),
            )
            "FullSizeImageRequest" -> imageManager.sendFullSizeImage(
                json.getJSONObject("input").getString("id"),
            )
            else -> {
                println("Message Not Found")
            }
        }
    }
}