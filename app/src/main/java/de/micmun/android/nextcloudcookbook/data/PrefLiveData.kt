/*
 * PrefLiveData.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

/**
 * LiveData for preferences.
 *
 * @author MicMun
 * @version 1.2, 02.08.20
 */

abstract class SharedPreferenceLiveData<T>(val sharedPrefs: SharedPreferences,
                                           private val key: String,
                                           private val defValue: T) : LiveData<T>() {

   abstract fun getValueFromPreferences(key: String, defValue: T): T

   private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
      if (key == this.key) {
         value = getValueFromPreferences(key, defValue)
      }
   }

   override fun onActive() {
      super.onActive()
      value = getValueFromPreferences(key, defValue)
      sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
   }

   override fun onInactive() {
      sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
      super.onInactive()
   }
}

// Implementations

// Boolean
class SharedPreferenceBooleanLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Boolean) :
   SharedPreferenceLiveData<Boolean>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean =
      sharedPrefs.getBoolean(key, defValue)
}

// String
class SharedPreferenceStringLiveData(sharedPrefs: SharedPreferences, key: String, defValue: String) :
   SharedPreferenceLiveData<String>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: String): String =
      sharedPrefs.getString(key, defValue) ?: ""
}

// Int
class SharedPreferenceIntLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Int) :
   SharedPreferenceLiveData<Int>(sharedPrefs, key, defValue) {
   override fun getValueFromPreferences(key: String, defValue: Int): Int = sharedPrefs.getInt(key, defValue)
}

fun SharedPreferences.booleanLiveData(key: String, defValue: Boolean): SharedPreferenceLiveData<Boolean> {
   return SharedPreferenceBooleanLiveData(this, key, defValue)
}

fun SharedPreferences.stringLiveData(key: String, defValue: String): SharedPreferenceLiveData<String> {
   return SharedPreferenceStringLiveData(this, key, defValue)
}

fun SharedPreferences.intLiveData(key: String, defValue: Int): SharedPreferenceLiveData<Int> {
   return SharedPreferenceIntLiveData(this, key, defValue)
}
