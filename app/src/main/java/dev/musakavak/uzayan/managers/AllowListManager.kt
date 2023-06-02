package dev.musakavak.uzayan.managers

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import dev.musakavak.uzayan.models.AllowList
import dev.musakavak.uzayan.services.UzayanForegroundService

class AllowListManager(private val sp: SharedPreferences) {
    fun getAllowList(): AllowList {
        val allowListString = sp.getString("allow_list_json", null)
        return if (!allowListString.isNullOrBlank())
            Gson().fromJson(allowListString, AllowList::class.java)
        else AllowList()
    }

    fun saveAllowList(allowList: AllowList) {
        UzayanForegroundService.setActions(allowList)
        val allowListString = Gson().toJson(allowList)
        sp.edit {
            putString("allow_list_json", allowListString)
            apply()
        }
    }
}