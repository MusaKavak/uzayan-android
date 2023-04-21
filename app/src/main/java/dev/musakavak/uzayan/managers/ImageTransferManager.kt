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

class ImageTransferManager(private val context: Context) {
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.SIZE
    )
    private val imageTool = Base64Tool()
    private val contentResolver = context.contentResolver

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

    fun sendFullSizeImage(id: String?) {
        if (id.isNullOrBlank()) return
        id.toLongOrNull()?.let {
            val uri = getUri(it)
            val inputStream = context.contentResolver.openInputStream(uri)
            // largeFileTool.sendWithInputStream(inputStream)
        }
    }

    private fun sendThumbnail(cursor: Cursor) {
        TcpSocket.emit("ImageThumbnail", getThumbnail(cursor))
    }

    private fun getThumbnail(cursor: Cursor): ImageThumbnail {
        val id = cursor.getLong(
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        )
        val name =
            cursor.getString(
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
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