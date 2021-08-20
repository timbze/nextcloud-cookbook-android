package de.micmun.android.nextcloudcookbook.ui.connectapi

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModel

class ConnectApiViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConnectApiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConnectApiViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}