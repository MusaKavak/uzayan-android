package dev.musakavak.uzayan.managers

import android.os.Environment
import dev.musakavak.uzayan.socket.TcpSocket
import dev.musakavak.uzayan.tools.LargeFileTool
import java.io.File
import dev.musakavak.uzayan.models.File as ModelFile

class FileManager {

    private val largeFileTool = LargeFileTool()
    private var externalStoragePath: String? = null
    fun sendFileSystem(path: String) {
        val fileSystemToSend: ModelFile = if (path.isNotEmpty() && path != externalStoragePath) {
            getFormattedFile(File(path))
        } else {
            getFormattedFile(Environment.getExternalStorageDirectory())
                .also {
                    it.isRoot = true
                    externalStoragePath = it.path
                }
        }
        TcpSocket.emit("FileSystem", fileSystemToSend)
    }

    fun sendFile(path: String) {
        val fileToSend = File(path)
        if (fileToSend.exists() &&
            fileToSend.isFile &&
            fileToSend.canRead()
        ) {
            largeFileTool.sendWithInputStream(fileToSend.inputStream())
        }
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
            fileToSend.extension,
            fileToSend.absolutePath,
            fileToSend.isFile,
            fileToSend.isHidden,
            fileToSend.length(),
            null,
            null,
            null
        )
    }

}