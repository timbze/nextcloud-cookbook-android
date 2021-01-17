/*
 * PreferenceRepositoryory.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data

import android.app.Application
import androidx.preference.PreferenceManager

/**
 * Manages the reading of the preferences.
 *
 * @author MicMun
 * @version 1.3, 02.08.20
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
            return instance
         }
      }
   }

   fun getRecipeDirectory(): SharedPreferenceLiveData<String> {
      return sharedPreferences.stringLiveData(Pref.RECIPE_DIR, "")
   }

   fun setRecipeDirectory(recipeDirectory: String) {
      sharedPreferences.edit().putString(Pref.RECIPE_DIR, recipeDirectory).apply()
   }

   fun getHiddenFolder(): SharedPreferenceLiveData<Boolean> {
      return sharedPreferences.booleanLiveData(Pref.HIDDEN_FOLDER, false)
   }

   fun setHiddenFolder(hiddenFolder: Boolean) {
      sharedPreferences.edit().putBoolean(Pref.HIDDEN_FOLDER, hiddenFolder).apply()
   }

   fun getTheme() = sharedPreferences.intLiveData(Pref.THEME, 0)
   fun getThemeSync() = sharedPreferences.getInt(Pref.THEME, 0)
   fun setTheme(theme: Int) {
      sharedPreferences.edit().putInt(Pref.THEME, theme).apply()
   }

   fun getSort() = sharedPreferences.intLiveData(Pref.SORT, 0)
   fun getSortSync() = sharedPreferences.getInt(Pref.SORT, 0)
   fun setSort(sort: Int) {
      sharedPreferences.edit().putInt(Pref.SORT, sort).apply()
   }

   fun isStorageAccessed(): SharedPreferenceLiveData<Boolean> {
      return sharedPreferences.booleanLiveData(Pref.STORAGE_ACCESS, false)
   }

   fun isStorageAccessedSync() = sharedPreferences.getBoolean(Pref.STORAGE_ACCESS, false)

   fun setStorageAccess(storageAccess: Boolean) {
      sharedPreferences.edit().putBoolean(Pref.STORAGE_ACCESS, storageAccess).apply()
   }
}
