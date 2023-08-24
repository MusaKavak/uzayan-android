package dev.musakavak.uzayan.managers

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class FileTransferManager {

    suspend fun sendFile(
        path: String,
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
            val buf = ByteArray(4096)
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

    suspend fun receiveFile(
        path: String,
        size: Long,
        socketIn: InputStream,
        socketOut: OutputStream,
    ) = withContext(Dispatchers.IO) {
        try {
            val fileToCreate = File(path)

            if (fileToCreate.createNewFile()) {
                socketOut.write(100)
            } else {
                socketOut.write(101)
            }

            val fileOutput = fileToCreate.outputStream()
            var bytesReceived: Long = 0

            val progressTracker = launch(Dispatchers.IO) {
                while (true) {
                    val progress = ((bytesReceived.toFloat() / size.toFloat() * 100.0).toInt())
                    println("progress:$progress")
                    if (progress >= 99) {
                        println("$bytesReceived / $size")
                        break
                    }
                    delay(1000L)
                }
            }

            val buffer = ByteArray(4096)
            while (true) {
                val bytesRead = socketIn.read(buffer)
                fileOutput.write(buffer, 0, bytesRead)
                bytesReceived += bytesRead
                if (bytesReceived >= size) {
                    size
                    break
                }
            }

            fileOutput.close()
            progressTracker.cancelAndJoin()
            socketOut.write(100)
        } catch (e: Exception) {
            Log.e("FileDown", "Error", e)
        }
    }
}