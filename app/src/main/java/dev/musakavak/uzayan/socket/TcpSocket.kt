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
import java.io.OutputStream
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.ServerSocket

class TcpSocket(
    private val actions: Actions
) {

    private var socketServer: ServerSocket? = null
    private val tag = "TcpSocketServer"

    companion object {
        private val gson = Gson()
        private var writerList = mutableListOf<WriterListObject>()
        var largeFileStream: WriterListObject? = null
        var socketPort: Int? = null

        private val scope = CoroutineScope(Dispatchers.IO)
        fun <T> emit(event: String, input: T) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    writerList.forEach {
                        val message = gson.toJson(ConnectionObject(event, input))
                        it.writer.println(message)
                        it.writer.flush()
                    }
                }
            }
        }

        data class WriterListObject(
            val ip: String,
            val writer: PrintWriter,
            val stream: OutputStream
        )
    }

    suspend fun initializeSocketServer() {
        withContext(Dispatchers.IO) {
            if (socketServer == null) {
                val newServer = ServerSocket()
                newServer.bind(InetSocketAddress(0))
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
                    val address = "${socket?.inetAddress?.hostName}:${socket.port}"
                    Log.w(tag, "New Tcp Socket Connection From: $address")
                    withContext(Dispatchers.IO) {
                            val outputStream = socket.getOutputStream()
                        writerList.add(
                            WriterListObject(
                                address,
                                PrintWriter(outputStream),
                                outputStream
                            )
                        )
                        listenForMessages(socket.getInputStream(), address)
                    }
                    Log.w(tag, "Tcp Socket From: $address Is Disconnected")
                }
            }
        }
    }

    private fun listenForMessages(inputStream: InputStream, ip: String) {
        BufferedReader(InputStreamReader(inputStream)).use { buffer ->
            buffer.lineSequence().forEach { sequence ->
                invoke(sequence, ip)
            }
        }
    }

    private fun invoke(input: String, ip: String) {
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
                "FileRequest" -> actions.fileRequest(json)
                "FullSizeImageRequest" ->
                    if (prepareForLargeFile(ip)) actions.fullSizeImageRequest(json)
                else -> {
                    println("Message Not Found")
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error: ", e)
        }
    }

    private fun prepareForLargeFile(ip: String): Boolean {
        val i = writerList.indexOfFirst { it.ip == ip }

        return if (i != -1) {
            largeFileStream = writerList[i]
            writerList.removeAt(i)
            true
        } else false
    }

}
