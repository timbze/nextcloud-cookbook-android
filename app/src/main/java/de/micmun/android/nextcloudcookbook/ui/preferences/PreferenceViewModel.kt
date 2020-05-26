/*
 * PreferenceViewModel.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.micmun.android.nextcloudcookbook.data.PreferenceDao
import de.micmun.android.nextcloudcookbook.data.SharedPreferenceLiveData

/**
 * ViewModel for preferences.
 *
 * @author MicMun
 * @version 1.1, 26.05.20
 */
class PreferenceViewModel(application: Application) : AndroidViewModel(application) {
   private val prefDao = PreferenceDao.getInstance(application)

   internal val recipeDirectory: SharedPreferenceLiveData<String>
   internal val theme: SharedPreferenceLiveData<Int>

   init {
      recipeDirectory = prefDao.getRecipeDirectory()
      theme = prefDao.getTheme()
   }

   fun setRecipeDirectory(recipeDirectory: String) {
      prefDao.setRecipeDirectory(recipeDirectory)
   }

   fun setTheme(theme: Int) {
      prefDao.setTheme(theme)
   }
}
