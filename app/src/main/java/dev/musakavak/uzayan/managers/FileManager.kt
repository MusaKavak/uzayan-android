package dev.musakavak.uzayan.managers

import android.os.Environment
import dev.musakavak.uzayan.socket.Emitter
import java.io.File
import java.io.InputStream
import kotlin.io.path.moveTo
import dev.musakavak.uzayan.models.File as ModelFile

class FileManager {

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
        Emitter.emit("FileSystem", fileSystemToSend)
    }

    fun getFileToSend(path: String): InputStream? {
        val fileToSend = File(path)
        if (fileToSend.exists() &&
            fileToSend.isFile &&
            fileToSend.canRead()
        ) {
            return fileToSend.inputStream()
        }
        return null
    }

    fun deleteFile(path: String) {
        val fileToDelete = File(path)
        if (fileToDelete.exists() &&
            fileToDelete.isFile
        ) {
            fileToDelete.delete()
            fileToDelete.parent?.let { sendFileSystem(it) }
        }
    }

    fun moveFile(sourcePath: String, targetPath: String) {
        val source = File(sourcePath)
        if (source.exists() && source.isFile) {
            val targetFile = File(targetPath)
            source.toPath().moveTo(targetFile.toPath(), false)
            targetFile.parent?.let { sendFileSystem(it) }
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
            false,
            null,
            null
        )
    }

}