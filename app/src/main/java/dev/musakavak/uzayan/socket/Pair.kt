package dev.musakavak.uzayan.socket

import com.google.gson.Gson
import dev.musakavak.uzayan.models.NetworkMessage
import dev.musakavak.uzayan.models.PairObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket

suspend fun sendPairRequest(
    ip: String,
    localPort: Int,
    destinationPort: Int,
    pairCode: Int,
    name: String
) = withContext(Dispatchers.IO) {
        val socket = Socket(ip,destinationPort)
        val payload = PairObject(localPort, pairCode, name)
        val pairObject = NetworkMessage("Pair", payload)

        val out = socket.getOutputStream()
        out.write(Gson().toJson(pairObject).toByteArray())
        out.flush()
}

