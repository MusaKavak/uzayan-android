package dev.musakavak.uzayan.tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.util.Base64
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream

class Base64Tool {


    fun fromBitmap(bitmap: Bitmap?): String? {
        bitmap?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }
        return null
    }

    fun fromIcon(icon: Icon?, context: Context): String? {
        icon?.let {
            val bitmap = it.loadDrawable(context)?.toBitmap()
            return this.fromBitmap(bitmap)
        }
        return null
    }
}