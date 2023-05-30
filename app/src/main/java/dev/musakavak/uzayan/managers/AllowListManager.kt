package dev.musakavak.uzayan.managers

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.musakavak.uzayan.models.AllowList
import dev.musakavak.uzayan.services.UzayanForegroundService

class AllowListManager(private val sp: SharedPreferences) {
    fun getAllowList(): AllowList {
        return AllowList(
            sp.getBoolean("alw_media_sessions", false),
            sp.getBoolean("alw_notifications", false),
            sp.getBoolean("alw_notification_transfer", false),
            sp.getBoolean("alw_file_transfer", false),
            sp.getBoolean("alw_image_transfer", false),
        )
    }

    fun saveAllowList(al: AllowList) {
        sp.edit {
            putBoolean("alw_media_sessions", al.mediaSessions)
            putBoolean("alw_notifications", al.notifications)
            putBoolean("alw_notification_transfer", al.notificationTransfer)
            putBoolean("alw_file_transfer", al.fileTransfer)
            putBoolean("alw_image_transfer", al.imageTransfer)
        }
        UzayanForegroundService.setActions(al)
    }
}