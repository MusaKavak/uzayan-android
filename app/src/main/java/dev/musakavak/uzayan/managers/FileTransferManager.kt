package dev.musakavak.uzayan.managers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class FileTransferManager {
    private val chunkSize = 4096

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

    suspend fun createFile(path: String, size: Long, input: InputStream, output: OutputStream) =
        withContext(Dispatchers.IO) {
            try {
                val fileToCreate = File(path)
                if (fileToCreate.exists()) output.write(103)
                else {
                    val status = fileToCreate.createNewFile()
                    if (status) {
                        if (!fileToCreate.canWrite()) output.write(102)
                        else {
                            val fileOutput = fileToCreate.outputStream()
                            val buffer = ByteArray(chunkSize)
                            var allReceivedBytes: Long = 0
                            output.write(100)
                            while (true) {
                                val receivedBytes = input.read(buffer)
                                fileOutput.write(buffer, 0, receivedBytes)
                                allReceivedBytes += receivedBytes
                                if (allReceivedBytes == size) break
                            }
                            fileOutput.close()
                        }
                    }
                }
                output.write(99)
            } catch (e: Exception) {
                println(e)
            }
        }
}