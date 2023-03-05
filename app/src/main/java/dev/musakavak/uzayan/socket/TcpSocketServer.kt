package dev.musakavak.uzayan.socket

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class TcpSocketServer(
    private val actions: Actions
) {

    private var socketServer: ServerSocket? = null
    private val tag = "TcpSocketServer"

    suspend fun initializeSocketServer() {
        withContext(Dispatchers.IO) {
            if (socketServer == null) {
                socketServer = ServerSocket(34724)
                listenForConnection()
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private fun listenForConnection() {
        socketServer?.let {
            while (!it.isClosed) {
                val socket = it.accept()
                scope.launch {
                    Log.w(tag, "New Tcp Socket Connection From: ${socket?.inetAddress?.hostName}")
                    listenForMessages(socket)
                }
            }
        }
    }

    private fun listenForMessages(socket: Socket) {
        BufferedReader(InputStreamReader(socket.getInputStream())).use { buffer ->
            buffer.lineSequence().forEach { sequence ->
                invoke(sequence)
            }
        }
    }

    private fun invoke(input: String) {
        try {
            val json = JSONObject(input)
            println("Received Packet With Message:  " + json.get("message"))

            when (json.get("message")) {
                //  "TestConnection" -> UdpSocket.emit("TestConnection", null)
                //  "Pair" -> UdpSocket.setAddress(json.getString("address"))
                "MediaSessionControl" -> actions.mediaSessionControl(json)
                "MediaSessionsRequest" -> actions.mediaSessionRequest()
                "NotificationAction" -> actions.notificationAction(json)
                "NotificationsRequest" -> actions.notificationsRequest()
                "ImageThumbnailRequest" -> actions.imageThumbnailRequest(json)
                "FullSizeImageRequest" -> actions.fullSizeImageRequest(json)
                else -> {
                    println("Message Not Found")
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e(tag, "Error: ", e)
        }
    }
}
