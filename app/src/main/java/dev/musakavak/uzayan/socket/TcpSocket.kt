package dev.musakavak.uzayan.socket

import android.util.Log
import com.google.gson.Gson
import dev.musakavak.uzayan.models.ConnectionObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

class TcpSocket(
    private val actions: Actions
) {

    private var socketServer: ServerSocket? = null
    private val tag = "TcpSocketServer"

    companion object {
        private val gson = Gson()
        private var out: PrintWriter? = null
        var socketPort: Int? = null

        private val scope = CoroutineScope(Dispatchers.IO)
        fun <T> emit(event: String, input: T) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    out?.let {
                        val message = gson.toJson(ConnectionObject(event, input))
                        it.println(message)
                        it.flush()
                    }
                }
            }
        }
    }

    suspend fun initializeSocketServer() {
        withContext(Dispatchers.IO) {
            if (socketServer == null) {
                val newServer = ServerSocket(34724)
                socketPort = newServer.localPort
                socketServer = newServer
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
                    val address = socket?.inetAddress?.hostName
                    Log.w(tag, "New Tcp Socket Connection From: $address")
                    withContext(Dispatchers.IO) {
                        out = PrintWriter(socket.getOutputStream())
                        listenForMessages(socket.getInputStream())
                    }
                    Log.w(tag, "Tcp Socket From: $address Is Disconnected")
                }
            }
        }
    }

    private fun listenForMessages(inputStream: InputStream) {
        BufferedReader(InputStreamReader(inputStream)).use { buffer ->
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
                "TestConnection" -> emit("TestConnection", null)
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
