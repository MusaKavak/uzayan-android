package dev.musakavak.uzayan.managers

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dev.musakavak.uzayan.models.ImageThumbnail
import dev.musakavak.uzayan.socket.Client
import dev.musakavak.uzayan.tools.Base64Tool

class ImageManager(private val context: Context) {
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED
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
        val idAsLong = id.toLongOrNull()
        idAsLong?.let {
            val uri = getUri(it)
            imageTool.fromUri(uri, context, 100)
        }
    }

    private fun sendThumbnail(cursor: Cursor) {
        Client.emit("ImageThumbnail", getThumbnail(cursor))
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

        val thumbnail = contentResolver.loadThumbnail(
            getUri(id),
            android.util.Size(120, 120), null
        )
        return ImageThumbnail(
            id.toString(),
            imageTool.fromBitmap(thumbnail, 100),
            name,
            cursor.position,
            date
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