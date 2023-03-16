package dev.musakavak.uzayan.tools

import dev.musakavak.uzayan.socket.TcpSocket
import kotlinx.coroutines.*
import java.io.InputStream

class LargeFileTool {
    private val chunkSize = 4000
    private val scope = CoroutineScope(Dispatchers.IO)
    fun sendWithInputStream(_stream: InputStream?) {
        scope.launch {
            withContext(Dispatchers.IO) {
                _stream?.let { stream ->
                    TcpSocket.largeFileStream?.let { socket ->
                        val buffer = ByteArray(chunkSize)
                        while (stream.read(buffer) != -1) {
                            socket.stream.write(buffer)
                            socket.stream.flush()
                        }
                        delay(1000L)
                        socket.stream.write("@@@".toByteArray())
                        socket.stream.flush()

                    }
                }
            }
        }
    }

}