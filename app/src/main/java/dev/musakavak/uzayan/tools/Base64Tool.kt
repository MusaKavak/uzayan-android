package dev.musakavak.uzayan.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.Base64
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream

class Base64Tool {


    fun fromBitmap(bitmap: Bitmap?, quality: Int): String? {
        bitmap?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            val byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }
        return null
    }

    fun fromIcon(icon: Icon?, context: Context): String? {
        icon?.let {
            val bitmap = it.loadDrawable(context)?.toBitmap()
            return this.fromBitmap(bitmap, 50)
        }
        return null
    }

    fun fromUri(uri: Uri, context: Context,quality: Int): String? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        return fromBitmap(bitmap, quality)
    }
}