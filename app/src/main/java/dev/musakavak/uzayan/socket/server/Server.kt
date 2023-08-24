package dev.musakavak.uzayan.socket.server

import android.util.Log
import dev.musakavak.uzayan.socket.Actions
import dev.musakavak.uzayan.socket.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.TimeUnit

class Server(private val actions: Actions) {
    private var server: ServerSocket? = null
    val port get() = server?.localPort
    private val scope = CoroutineScope(Dispatchers.IO)

    fun initialize(secureConnection: Boolean) {
        println("Creating Server")
        val startTime = System.nanoTime()
        server = if (secureConnection) getSSLServerSocket() else getSimpleServerSocket()
        val endTime = System.nanoTime()
        val time = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)
        println("Server Created in: $time milliseconds")
    }

    fun listen() {
        scope.launch {
            if (server == null) return@launch
            while (!server!!.isClosed) {
                Log.w("Sockett Simple", "Listening for new client")
                val socket = server!!.accept()
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
                    when (inS.read()) {
                        StreamType.MAIN.code -> {
                            Emitter.writer = PrintWriter(outS)
                            listenMainStream(inS, actions)
                        }

                        StreamType.SEND_FILE.code -> listenFileStream(
                            inS,
                            outS,
                            actions::sendFile
                        )

                        StreamType.RECEIVE_FILE.code -> listenFileStream(
                            inS,
                            outS,
                            actions::receiveFile
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
        inS.bufferedReader().lineSequence().forEach {
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

    private suspend fun listenFileStream(
        inS: InputStream,
        outS: OutputStream,
        action: suspend (
            p: String,
            i: InputStream,
            o: OutputStream
        ) -> Unit
    ) {
        inS.bufferedReader().lineSequence().forEach {
            if (it.isNotEmpty() && it != "***DONE***")
                action(it, inS, outS)
        }
    }

    fun close() {
        scope.cancel()
        server?.close()
    }

    private enum class StreamType(val code: Int) {
        MAIN(200),
        SEND_FILE(201),
        RECEIVE_FILE(202)
    }
}