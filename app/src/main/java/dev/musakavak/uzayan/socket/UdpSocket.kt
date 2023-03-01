package dev.musakavak.uzayan.socket

import com.google.gson.Gson
import dev.musakavak.uzayan.models.ConnectionObject
import kotlinx.coroutines.*
import kotlin.math.ceil
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpSocket {
    companion object {
        private var udpSocket: DatagramSocket? = null
        private var address: InetAddress = InetAddress.getByName("192.168.1.109")
        private const val chunkSize = 1500
        private var packetId = 0

        fun initializeSocket(): DatagramSocket? {
            if (udpSocket == null) {
                udpSocket = DatagramSocket(34724)
                return udpSocket
            }
            return null
        }


        private val scope = CoroutineScope(Dispatchers.IO)
        fun <T> emit(message: String, payload: T) {
            val stringToEmit = Gson().toJson(ConnectionObject(message, payload))
            scope.launch {
                    sendMessage(stringToEmit, packetId)
                    if (packetId == 99) packetId = 0 else packetId++
            }
        }

        private suspend fun sendMessage(message: String, id: Int) {
            withContext(Dispatchers.IO) {
                udpSocket?.let { socket ->
                    var countOfChunks = 0
                    val stream = message
                        .toByteArray()
                        .also {
                            countOfChunks = ceil(it.size.toDouble() / chunkSize.toDouble())
                                .toInt()
                        }
                        .inputStream()

                    var chunk = ByteArray(chunkSize)
                    var i = stream.read(chunk)
                    var currentChunk = 1
                    while (i != -1) {
                        val chunkHeader = "${id}@${countOfChunks}@${currentChunk}@".toByteArray()
                        val chunkToSend = chunkHeader + chunk
                        val dp = DatagramPacket(
                            chunkToSend, chunkToSend.size, address, 34724
                        )
                        socket.send(dp)
                        chunk = ByteArray(chunkSize)
                        i = stream.read(chunk)
                        currentChunk++
                    }

                    stream.close()
                }
            }
        }


        fun setAddress(a: String) {
            address = InetAddress.getByName(a)
        }

    }
}
