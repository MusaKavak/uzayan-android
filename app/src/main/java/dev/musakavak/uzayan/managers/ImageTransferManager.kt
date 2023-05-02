package dev.musakavak.uzayan.managers

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import dev.musakavak.uzayan.models.ImageThumbnail
import dev.musakavak.uzayan.socket.TcpSocket
import dev.musakavak.uzayan.tools.Base64Tool
import java.io.InputStream

class ImageTransferManager(private val context: Context) {
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.SIZE
    )
    private val imageTool = Base64Tool()
    private val contentResolver = context.contentResolver
    private val supportedFormats = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    fun sendSlice(start: Int?, length: Int?) {
        if (start == null || length == null) return
        invokeCursor {
            if (it.moveToPosition(start)) {
                for (i in 1..length) {
                    sendThumbnail(it)
                    if (!it.moveToNext()) break
                }
            }
        }
    }

    fun getImageInputStream(id: String?): InputStream? {
        if (id.isNullOrBlank()) return null
        id.toLongOrNull()?.let {
            val uri = getUri(it)
            return context.contentResolver.openInputStream(uri)
        }
        return null
    }

    private fun sendThumbnail(cursor: Cursor) {
        val thumbnail = getThumbnail(cursor)
        thumbnail?.let {
            TcpSocket.emit("ImageThumbnail", it)
        }
    }

    private fun getThumbnail(cursor: Cursor): ImageThumbnail? {
        val name =
            cursor.getString(
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            )

        val imageFormat = name.substringAfterLast('.', "").lowercase()
        if (!supportedFormats.contains(imageFormat)) return null

        val id = cursor.getLong(
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        )

        val date =
            cursor.getLong(
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            )
        val size =
            cursor.getLong(
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            )

        val thumbnail = contentResolver.loadThumbnail(
            getUri(id),
            Size(120, 120), null
        )

        return ImageThumbnail(
            id.toString(),
            imageTool.fromBitmap(thumbnail),
            name,
            cursor.position,
            date,
            size
        )
    }

    private fun getUri(id: Long): Uri {
        return ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
        )
    }

    private fun invokeCursor(callback: (cursor: Cursor) -> Unit) {
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )
        cursor?.let {
            callback(cursor)
            cursor.close()
        }
    }

}