package dev.musakavak.uzayan.socket

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import dev.musakavak.uzayan.managers.MediaSessionManager
import dev.musakavak.uzayan.models.ConnectionObject
import dev.musakavak.uzayan.models.MediaSessionControl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.StringReader
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
                val x = MediaSessionControl(
                    json.getJSONObject("input").getString("token"),
                    json.getJSONObject("input").getString("action"),
                )
                mediaSessionManager.mediaSessionControl(x)
            }
            else -> {
                println("Message Not Found")
            }
        }
    }
}