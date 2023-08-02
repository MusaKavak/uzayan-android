package dev.musakavak.uzayan.socket.server

import android.util.Log
import dev.musakavak.uzayan.socket.Actions
import dev.musakavak.uzayan.socket.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket

class Server(secureConnection: Boolean, private val actions: Actions) {
    private val server = if (secureConnection) getSSLServerSocket() else getSimpleServerSocket()
    val port get() = server.localPort
    private val scope = CoroutineScope(Dispatchers.IO)

    fun listen() {
        scope.launch {
            while (!server.isClosed) {
                Log.w("Sockett Simple", "Listening for new client")
                val socket = server.accept()
                scope.launch {
                    val address = "${socket?.inetAddress?.hostName}:${socket.port}"
                    Log.w("Sockett Simple", "New Tcp Socket Connection From: $address")
                    handleConnection(socket)
                    Log.w("Sockett Simple", "Tcp Socket From: $address Is Disconnected")
                }
            }
        }
    }

    private suspend fun handleConnection(socket: Socket) {
        val input = socket.getInputStream()
        val output = socket.getOutputStream()
        try {
            input.use { inS ->
                output.use { outS ->
                    val code = inS.read()
                    code
                    when (code) {
                        StreamType.MAIN.code -> {
                            Emitter.writer = PrintWriter(outS)
                            listenMainStream(inS, actions)
                        }

                        StreamType.FILE_INPUT.code -> listenFileStream(
                            inS,
                            outS,
                            actions::createFileRequest
                        )

                        StreamType.FILE_OUTPUT.code -> listenFileStream(
                            inS,
                            outS,
                            actions::fileRequest
                        )

                        else -> {}
                    }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            output.close()
            input.close()
            socket.close()
        }
    }

    private fun listenMainStream(inS: InputStream, actions: Actions) {
        BufferedReader(InputStreamReader(inS)).use { buffer ->
            buffer.lineSequence().forEach {
                val json = JSONObject(it)
                when (json.get("message")) {
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
    }

    private suspend fun listenFileStream(
        inS: InputStream,
        outS: OutputStream,
        action: suspend (
            j: JSONObject,
            i: InputStream,
            o: OutputStream
        ) -> Unit
    ) {
        while (true) {
            val message =
                BufferedReader(InputStreamReader(inS)).readLine()
            if (message == "done") break
            action(JSONObject(message), inS, outS)
        }
    }

    fun close() {
        scope.cancel()
        server.close()
    }

    private enum class StreamType(val code: Int) {
        MAIN(200),
        FILE_OUTPUT(201),
        FILE_INPUT(202)
    }
}