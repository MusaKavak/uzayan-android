package dev.musakavak.uzayan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.musakavak.uzayan.managers.AllowListManager

class AllowListVMFactory(private val allowListManager: AllowListManager) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllowListViewModel::class.java))
            return AllowListViewModel(allowListManager) as T
        throw IllegalArgumentException("ViewModel Not Found")
    }
}