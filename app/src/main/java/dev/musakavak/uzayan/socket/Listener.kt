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

    }
}