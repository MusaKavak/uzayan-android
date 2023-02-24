package dev.musakavak.uzayan.socket

import com.google.gson.Gson
import dev.musakavak.uzayan.models.ConnectionObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpSocket {
    companion object {
        private var udpSocket: DatagramSocket? = null
        private var address: InetAddress = InetAddress.getByName("192.168.1.112")

        fun initializeSocket(): DatagramSocket?{
            if (udpSocket == null) {
                udpSocket = DatagramSocket(34724)
                return  udpSocket
            }
            return null
        }

        private val scope = CoroutineScope(Dispatchers.IO)

        fun <T> emit(message: String, payload: T) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    val stringToEmit = Gson().toJson(ConnectionObject(message, payload))
                    sendMessage(stringToEmit)
                }
            }
        }

        fun sendMessage(message: String) {
            udpSocket?.let {
                val byteArray = message.toByteArray()
                val dp = DatagramPacket(
                    byteArray,
                    byteArray.size,
                    address,
                    34724
                )
                it.send(dp)
            }
        }

        fun setAddress(a: String) {
            address = InetAddress.getByName(a)
        }

    }
}
