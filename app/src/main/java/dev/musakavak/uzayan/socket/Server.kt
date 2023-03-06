package dev.musakavak.uzayan.socket

import android.util.Log
import dev.musakavak.uzayan.models.PairObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class Server(
    private val actions: Actions
) {

    private var socketServer: ServerSocket? = null
    private val tag = "TcpSocketServer"

    companion object{
        var pairObject: PairObject? = null
    }

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
                "TestConnection" -> Client.emit("TestConnection", null)
                "Pair" -> actions.pair(json)
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
