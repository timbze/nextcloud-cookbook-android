/*
 * PreferenceViewModel.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.micmun.android.nextcloudcookbook.settings.PreferenceDao
import de.micmun.android.nextcloudcookbook.settings.SharedPreferenceLiveData

/**
 * ViewModel for preferences.
 *
 * @author MicMun
 * @version 1.3, 07.04.21
 */
class PreferenceViewModel(application: Application) : AndroidViewModel(application) {
   private val prefDao = PreferenceDao.getInstance(application)

   internal val recipeDirectory: SharedPreferenceLiveData<String> = prefDao.getRecipeDirectory()
   internal val theme: SharedPreferenceLiveData<Int> = prefDao.getTheme()
   internal val storageAccesssed: SharedPreferenceLiveData<Boolean> = prefDao.isStorageAccessed()

   fun setRecipeDirectory(recipeDirectory: String) {
      prefDao.setRecipeDirectory(recipeDirectory)
   }

   fun setTheme(theme: Int) {
      prefDao.setTheme(theme)
   }

   fun setStorageAccessed(access: Boolean) {
      prefDao.setStorageAccess(access)
   }
}
