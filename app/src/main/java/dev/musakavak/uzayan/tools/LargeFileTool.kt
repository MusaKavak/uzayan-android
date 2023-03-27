package dev.musakavak.uzayan.tools

import dev.musakavak.uzayan.socket.TcpSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                        var totalBytesRead = 0
                        while (true) {
                            val bytesRead = stream.read(buffer)
                            if (bytesRead == -1) break
                            totalBytesRead += bytesRead
                            socket.stream.write(buffer, 0, bytesRead)
                            socket.stream.flush()
                        }
                    }
                }
            }
        }
    }

}