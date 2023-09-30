package dev.musakavak.uzayan.tools

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings

fun checkStorageAccessPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
        context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    } else
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else false
}

fun checkNotificationAccessPermission(context: Context): Boolean {
    return Settings
        .Secure
        .getString(context.contentResolver, "enabled_notification_listeners")
        .contains(context.packageName)
}