package dev.musakavak.uzayan.tools

import dev.musakavak.uzayan.socket.TcpSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class LargeFileTool {
    private val chunkSize = 4096
    private val scope = CoroutineScope(Dispatchers.IO)
    fun sendWithInputStream(_stream: InputStream?) {
        try {
            scope.launch {
                withContext(Dispatchers.IO) {
                    _stream?.let { stream ->
                        TcpSocket.fileStream?.let { socket ->
                            val buffer = ByteArray(chunkSize)
                            val outputStream = socket.getOutputStream()
                            while (true) {
                                val bytesRead = stream.read(buffer)
                                if (bytesRead == -1) break
                                outputStream.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

}