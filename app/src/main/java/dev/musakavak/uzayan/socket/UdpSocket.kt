package dev.musakavak.uzayan.socket

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpSocket {
    companion object {
        private var udpSocket: DatagramSocket? = null
        private var address: InetAddress = InetAddress.getByName("192.168.1.111")

        fun initializeSocket(): DatagramSocket?{
            if (udpSocket == null) {
                udpSocket = DatagramSocket(34724)
                return  udpSocket
            }
            return null
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
