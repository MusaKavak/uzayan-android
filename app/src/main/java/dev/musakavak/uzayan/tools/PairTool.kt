package dev.musakavak.uzayan.tools

import com.google.gson.Gson
import dev.musakavak.uzayan.models.NetworkMessage
import dev.musakavak.uzayan.models.PairObject
import dev.musakavak.uzayan.socket.TcpSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class PairTool {

    fun sendPairRequest(ip: String, port: Int, pairCode: String, name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val udp = DatagramSocket()
                TcpSocket.socketPort?.let {
                    val pairInput = PairObject(it, pairCode, name)

                    val pairObject = NetworkMessage("Pair", pairInput)
                    val buf = Gson().toJson(pairObject).toByteArray()
                    val address = InetAddress.getByName(ip)
                    val packet = DatagramPacket(
                        buf,
                        buf.size,
                        address,
                        port
                    )
                    udp.send(packet)
                }
            }
        }
    }

}