package dev.musakavak.uzayan.managers

import android.os.Environment
import dev.musakavak.uzayan.socket.TcpSocket
import java.io.File
import dev.musakavak.uzayan.models.File as ModelFile

class FileManager {

    fun sendFile(path: String) {
        val fileToSend: File = if (path.isNotEmpty()) {
            File(path)
        } else {
            Environment.getExternalStorageDirectory()
        }

        TcpSocket.emit("File", getFormattedFile(fileToSend))
    }

    private fun getFormattedFile(file: File): ModelFile {
        val fileToFormat = getUnrelated(file)
        if (file.isDirectory) {
            val children = mutableListOf<ModelFile>()
            file.listFiles()?.forEach {
                children.add(getUnrelated(it))
            }
            fileToFormat.children = children
        }
        fileToFormat.parent = file.parentFile?.let { getUnrelated(it) }
        return fileToFormat
    }

    private fun getUnrelated(fileToSend: File): ModelFile {
        return ModelFile(
            fileToSend.name,
            fileToSend.absolutePath,
            fileToSend.isFile,
            fileToSend.isHidden,
            null,
            null
        )
    }

}