package de.micmun.android.nextcloudcookbook.util

import androidx.preference.PreferenceManager
import de.micmun.android.nextcloudcookbook.MainApplication

class CommonUtils {
    companion object {
        val sharedPreferences get() = PreferenceManager.getDefaultSharedPreferences(MainApplication.AppContext)
    }
}