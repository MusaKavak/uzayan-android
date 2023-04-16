package dev.musakavak.uzayan.managers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.Socket

class FileTransferManager {
    private val chunkSize = 4096
    private val scope = CoroutineScope(Dispatchers.IO)

    fun sendFile(file: File) {
        try {
//            scope.launch {
//                withContext(Dispatchers.IO) {
//                    _stream?.let { stream ->
//                        TcpSocket.fileStream?.let { socket ->
//                            val buffer = ByteArray(chunkSize)
//                            val outputStream = socket.getOutputStream()
//                            while (true) {
//                                val bytesRead = stream.read(buffer)
//                                if (bytesRead == -1) break
//                                outputStream.write(buffer, 0, bytesRead)
//                            }
//                        }
//                    }
//                }
//            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    suspend fun createFile(path: String, size: Long, socket: Socket) = withContext(Dispatchers.IO) {
        try {
            val out = socket.getOutputStream()
            val fileToCreate = File(path)
            if (fileToCreate.exists()) out.write(103)
            else {
                val status = fileToCreate.createNewFile()
                if (status) {
                    if (!fileToCreate.canWrite()) out.write(102)
                    else {
                        val socketInput = socket.getInputStream()
                        val fileOutput = fileToCreate.outputStream()
                        val buffer = ByteArray(chunkSize)
                        var allReceivedBytes = 0
                        out.write(100)
                        while (allReceivedBytes <= size) {
                            val receivedBytes = socketInput.read(buffer)
                            if (receivedBytes == 0) break
                            println("Received $receivedBytes bytes")
                            fileOutput.write(buffer, 0, receivedBytes)
                            allReceivedBytes += receivedBytes
                        }
                        fileOutput.close()
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }
}