/*
 * PreferenceRepositoryory.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager

/**
 * Manages the reading of the preferences.
 *
 * @author MicMun
 * @version 1.1, 26.05.20
 */
class PreferenceDao private constructor(application: Application) {
   private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

   companion object {
      @Volatile
      private var INSTANCE: PreferenceDao? = null

      fun getInstance(application: Application): PreferenceDao {
         synchronized(PreferenceDao::class) {
            var instance =
               INSTANCE

            if (instance == null) {
               instance = PreferenceDao(application)
               INSTANCE = instance
            }
            Log.d("PreferenceDao", "getInstance()")
            return instance
         }
      }
   }

   fun getRecipeDirectory(): SharedPreferenceLiveData<String> {
      Log.d("PreferenceDao", "Recipe directory = ${sharedPreferences.stringLiveData(Pref.RECIPE_DIR, "").value}")
      return sharedPreferences.stringLiveData(Pref.RECIPE_DIR, "")
   }

   fun setRecipeDirectory(recipeDirectory: String) {
      sharedPreferences.edit().putString(Pref.RECIPE_DIR, recipeDirectory).apply()
   }

   fun getTheme() = sharedPreferences.intLiveData(Pref.THEME, 0)
   fun getThemeSync() = sharedPreferences.getInt(Pref.THEME, 0)
   fun setTheme(theme: Int) {
      sharedPreferences.edit().putInt(Pref.THEME, theme).apply()
   }
}
