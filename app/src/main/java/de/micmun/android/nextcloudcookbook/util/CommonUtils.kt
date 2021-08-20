package de.micmun.android.nextcloudcookbook.util

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import de.micmun.android.nextcloudcookbook.MainApplication

class CommonUtils {
    companion object {
        val sharedPreferences: SharedPreferences get() = PreferenceManager.getDefaultSharedPreferences(MainApplication.AppContext)
    }
}