package dev.musakavak.uzayan.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import dev.musakavak.uzayan.managers.AllowListManager
import dev.musakavak.uzayan.models.AllowList
import kotlin.reflect.KMutableProperty1

class AllowListViewModel(private val allowListManager: AllowListManager) : ViewModel() {
    var allowList by mutableStateOf(allowListManager.getAllowList())

    fun <K> setAllowance(allowance: KMutableProperty1<AllowList, K>, value: K) {
        allowList = allowList.copy().also { allowance.set(it, value) }
        allowListManager.saveAllowList(allowList)
    }

    var isDialogVisible by mutableStateOf(false)
    var onConfirm by mutableStateOf({})
    var titleId by mutableIntStateOf(-1)
    var textId by mutableIntStateOf(-1)
    var icon by mutableStateOf<Painter?>(null)

    fun dismissDialog() {
        isDialogVisible = false
        onConfirm = {}
        titleId = -1
        textId = -1
        icon = null
    }
}