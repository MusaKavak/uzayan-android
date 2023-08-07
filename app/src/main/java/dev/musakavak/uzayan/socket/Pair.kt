package dev.musakavak.uzayan.socket

import com.google.gson.Gson
import dev.musakavak.uzayan.models.NetworkMessage
import dev.musakavak.uzayan.models.PairObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

suspend fun sendPairRequest(
    ip: String,
    localPort: Int,
    destinationPort: Int,
    pairCode: Int,
    name: String
) = withContext(Dispatchers.IO) {

    val socket = DatagramSocket()

    val payload = PairObject(localPort, pairCode, name)

    val pairObject = NetworkMessage("Pair", payload)
    val buf = Gson().toJson(pairObject).toByteArray()
    val address = InetAddress.getByName(ip)

    val packet = DatagramPacket(
        buf,
        buf.size,
        address,
        destinationPort
    )

    socket.send(packet)
}

