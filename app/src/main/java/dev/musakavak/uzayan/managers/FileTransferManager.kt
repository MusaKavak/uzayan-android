package dev.musakavak.uzayan.managers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    suspend fun createFile(
        path: String,
        size: Long,
        input: InputStream,
        output: OutputStream,
        notificationManager: NotificationManager
    ) = withContext(Dispatchers.IO) {
        try {
            val fileToCreate = File(path)
            if (fileToCreate.exists()) {
                output.write(101)
                return@withContext
            }
            fileToCreate.createNewFile()
            if (!fileToCreate.canWrite()) {
                output.write(102)
                return@withContext
            }
            val fileOutput = fileToCreate.outputStream()
            val buffer = ByteArray(chunkSize)
            var bytesReceived: Long = 0
            val nf = notificationManager.createFileTransferNotification(
                "Receiving ${fileToCreate.name}",
                "${fileToCreate.name} Received"
            )
            output.write(100)

            val progressTracker = launch(Dispatchers.IO) {
                while (true) {
                    nf((bytesReceived.toFloat() / size * 100).toInt())
                    if (bytesReceived >= size) break
                    delay(1000L)
                }
            }

            while (true) {
                val bytesRead = input.read(buffer)
                fileOutput.write(buffer, 0, bytesRead)
                bytesReceived += bytesRead
                if (bytesReceived >= size) break
            }

            fileOutput.close()
            progressTracker.cancelAndJoin()
            nf(100)
            output.write(99)
        } catch (e: Exception) {
            println(e)
        }
    }
}