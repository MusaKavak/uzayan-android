package dev.musakavak.uzayan.socket

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpSocket {
    companion object {
        private var udpSocket: DatagramSocket? = null
        private var address: InetAddress = InetAddress.getByName("192.168.1.108")

        fun initializeSocket() {
            if (udpSocket == null) {
                udpSocket = DatagramSocket(34724)
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

        fun listenSocket() {
            udpSocket?.let {
                val packet = DatagramPacket(
                    ByteArray(1),
                    1,
                )
                it.receive(packet)

                println("Received Packet")
                println(packet)
                listenSocket()
            }
        }

        fun setAddress(a: String) {
            address = InetAddress.getByName(a)
        }

    }
}
