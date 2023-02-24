package dev.musakavak.uzayan.managers

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dev.musakavak.uzayan.models.Image
import dev.musakavak.uzayan.socket.UdpSocket
import dev.musakavak.uzayan.tools.Base64Tool

class ImageManager(private val context: Context) {
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED
    )
    private val base64Tool = Base64Tool()
    private val tag = "ImagesManager"

    fun sendSlice(start: Int?, length: Int?) {
        if (start == null || length == null) return
        invokeCursor {
            if (it.moveToPosition(start)) {
                for (i in 1..length) {
                    sendReducedImage(it)
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
            base64Tool.fromUri(uri, context, 100)
        }
    }

    private fun sendReducedImage(cursor: Cursor) {
        UdpSocket.emit("ReducedImage", getReducedImage(cursor))
    }

    private fun getReducedImage(cursor: Cursor): Image {
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
            ).toString()
        val base64 = base64Tool.fromUri(getUri(id), context, 50)
        return Image(
            id.toString(),
            base64,
            name,
            true
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