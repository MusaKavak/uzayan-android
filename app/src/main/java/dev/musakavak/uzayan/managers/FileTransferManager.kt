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

    suspend fun sendFile(
        path: String,
        socketIn: InputStream,
        socketOut: OutputStream
    ) = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            if (!file.exists()) return@withContext
            val fileSize = file.length()
            socketOut.write("$fileSize".toByteArray())


//
//            val progressTracker = launch(Dispatchers.IO) {
//                while (true) {
//                    println((bytesSent.toFloat() / size * 100).toInt())
//                    if (bytesSent >= size) break
//                    delay(1000L)
//                }
//            }
            val buf = ByteArray(chunkSize)
            var bytesSent = 0L

            val fileIn = file.inputStream()

            while (bytesSent < fileSize) {
                val bytesRead = fileIn.read(buf)
                socketOut.write(buf, 0, bytesRead)
                bytesSent += bytesRead
            }

            println("File Sent")
//
            fileIn.close()
//            socketIn.read()
//            progressTracker.cancelAndJoin()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    suspend fun createFile(
        path: String,
        size: Long,
        input: InputStream,
        output: OutputStream,
        notificationManager: NotificationManager?
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
            val nf = notificationManager?.createFileTransferNotification(
                "Receiving ${fileToCreate.name}",
                "${fileToCreate.name} Received"
            )

            output.write(100)

            val progressTracker = launch(Dispatchers.IO) {
                nf?.let {
                    while (true) {
                        nf((bytesReceived.toFloat() / size * 100).toInt())
                        if (bytesReceived >= size) break
                        delay(1000L)
                    }
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
            nf?.let { it(100) }
            output.write(99)
        } catch (e: Exception) {
            println(e)
        }
    }
}