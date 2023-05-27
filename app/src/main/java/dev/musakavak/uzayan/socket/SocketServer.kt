package dev.musakavak.uzayan.socket

import android.util.Log
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
import java.net.Socket

class SocketServer(private val actions: Actions) {
    private enum class StreamType(val code: Int) {
        MAIN(200),
        FILE_OUTPUT(201),
        FILE_INPUT(202)
    }

    private val tag = "TcpSocketServer"

    companion object {
        var port: Int? = null
    }

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (port == null) {
            val newServer = ServerSocket()
            newServer.bind(InetSocketAddress(0))
            port = newServer.localPort
            listenForConnection(newServer)
        }
    }

    private fun listenForConnection(server: ServerSocket) {
        while (!server.isClosed) {
            val socket = server.accept()
            CoroutineScope(Dispatchers.IO).launch {
                val address = "${socket?.inetAddress?.hostName}:${socket.port}"
                Log.w(tag, "New Tcp Socket Connection From: $address")
                handleConnection(socket)
                Log.w(tag, "Tcp Socket From: $address Is Disconnected")
            }
        }
    }

    private suspend fun handleConnection(socket: Socket) = withContext(Dispatchers.IO) {
        try {
            val inputStream = socket.getInputStream()
            when (inputStream.read()) {
                StreamType.MAIN.code -> {
                    Emitter.writer = PrintWriter(socket.getOutputStream())
                    listenMainStream(socket)
                }

                StreamType.FILE_INPUT.code -> listenFileStream(socket, actions::createFileRequest)
                StreamType.FILE_OUTPUT.code -> listenFileStream(socket, actions::fileRequest)
                else -> socket.close()
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun listenMainStream(socket: Socket) {
        try {
            BufferedReader(InputStreamReader(socket.getInputStream())).use { buffer ->
                buffer.lineSequence().forEach {
                    val json = JSONObject(it)
                    when (json.get("message")) {
                        "Pair" -> actions.pair(json)
                        "MediaSessionControl" -> actions.mediaSessionControl(json)
                        "MediaSessionsRequest" -> actions.mediaSessionRequest()
                        "NotificationAction" -> actions.notificationAction(json)
                        "NotificationsRequest" -> actions.notificationsRequest()
                        "ImageThumbnailRequest" -> actions.imageThumbnailRequest(json)
                        "FileSystemRequest" -> actions.fileSystemRequest(json)
                        "DeleteFileRequest" -> actions.deleteFileRequest(json)
                        "MoveFileRequest" -> actions.moveFileRequest(json)
                        else -> println("Message Not Found")
                    }
                }
            }
            Emitter.connectedClientName = null
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private suspend fun listenFileStream(
        socket: Socket,
        action: suspend (
            j: JSONObject,
            i: InputStream,
            o: OutputStream
        ) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val input = socket.getInputStream()
            val output = socket.getOutputStream()
            while (true) {
                val message =
                    BufferedReader(InputStreamReader(input)).readLine()
                if (message == "done") break
                action(JSONObject(message), input, output)
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
