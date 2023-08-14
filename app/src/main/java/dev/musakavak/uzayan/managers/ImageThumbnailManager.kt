package dev.musakavak.uzayan.managers

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Size
import dev.musakavak.uzayan.models.ImageThumbnail
import dev.musakavak.uzayan.socket.Emitter
import dev.musakavak.uzayan.tools.Base64Tool

class ImageThumbnailManager(private val contentResolver: ContentResolver) {
    private val base64Tool = Base64Tool()
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

    private fun sendThumbnail(cursor: Cursor) {
        val thumbnail = getThumbnail(cursor)
        thumbnail?.let {
            Emitter.emit("ImageThumbnail", it)
        }
    }

    private fun getThumbnail(cursor: Cursor): ImageThumbnail? {
        val name = cursor.getString(0)
        val imageFormat = name.substringAfterLast('.').lowercase()
        if (!supportedFormats.contains(imageFormat)) return null

        val path = cursor.getString(1)
        val id = cursor.getLong(2)
        val date = cursor.getLong(3)

        val thumbnail = contentResolver.loadThumbnail(
            getUri(id),
            Size(120, 120), null
        )

        return ImageThumbnail(
            name,
            base64Tool.fromBitmap(thumbnail)!!,
            path,
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
        val projection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED,
        )

        val cursor = contentResolver.query(
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