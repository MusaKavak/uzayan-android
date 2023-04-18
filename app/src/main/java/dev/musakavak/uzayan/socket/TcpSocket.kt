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
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class TcpSocket(
    private val actions: Actions
) {
    private val MAIN_STREAM = 200
    private val FILE_OUTPUT_STREAM = 201
    private val FILE_INPUT_STREAM = 202
    private var socketServer: ServerSocket? = null
    private val tag = "TcpSocketServer"

    companion object {
        private val gson = Gson()
        var socketPort: Int? = null

        var fileStream: Socket? = null
        private var mainStreamWriter: PrintWriter? = null

        private val scope = CoroutineScope(Dispatchers.IO)
        fun <T> emit(event: String, input: T) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    mainStreamWriter?.let {
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
                    handleConnection(socket)
                    Log.w(tag, "Tcp Socket From: $address Is Disconnected")
                }
            }
        }
    }

    private suspend fun handleConnection(socket: Socket) {
        try {
            val inputStream = socket.getInputStream()
            val streamType = inputStream.read()
            if (streamType == MAIN_STREAM) {
                mainStreamWriter = PrintWriter(socket.getOutputStream())
                listenForMessages(inputStream, ::mainStreamActions)
            }
            if (streamType == FILE_OUTPUT_STREAM) {
                fileStream = socket
                listenForMessages(inputStream, ::fileOutputActions)
            }
            if (streamType == FILE_INPUT_STREAM) {
                listenForFiles(socket)
            }
        } catch (e: Exception) {
            println(e.message)

        }
    }

    private fun listenForMessages(inputStream: InputStream, actions: (json: JSONObject) -> Unit) {
        try {
            BufferedReader(InputStreamReader(inputStream)).use { buffer ->
                buffer.lineSequence().forEach { sequence ->
                    val json = JSONObject(sequence)
                    println("New Message:  " + json.get("message"))
                    actions(json)
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }

    }

    private fun mainStreamActions(json: JSONObject) {
        when (json.get("message")) {
            "TestConnection" -> emit("TestConnection", null)
            "Pair" -> actions.pair(json)
            "MediaSessionControl" -> actions.mediaSessionControl(json)
            "MediaSessionsRequest" -> actions.mediaSessionRequest()
            "NotificationAction" -> actions.notificationAction(json)
            "NotificationsRequest" -> actions.notificationsRequest()
            "ImageThumbnailRequest" -> actions.imageThumbnailRequest(json)
            "FileSystemRequest" -> actions.fileSystemRequest(json)
            "DeleteFileRequest" -> actions.deleteFileRequest(json)
            "MoveFileRequest" -> actions.moveFileRequest(json)
            else -> {
                println("Message Not Found")
            }
        }
    }

    private fun fileOutputActions(json: JSONObject) {
        when (json.get("message")) {
            "CloseLargeFileStream" -> {
                fileStream?.close()
                fileStream = null
            }

            "FileRequest" -> actions.fileRequest(json)
            "FullSizeImageRequest" -> actions.fullSizeImageRequest(json)
        }
    }

    private suspend fun listenForFiles(socket: Socket) = withContext(Dispatchers.IO) {
        try {
            val input = socket.getInputStream()
            val output = socket.getOutputStream()
            while (true) {
                val message =
                    BufferedReader(InputStreamReader(input)).readLine()
                if (message == "done") break
                actions.createFile(JSONObject(message), input, output)
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
