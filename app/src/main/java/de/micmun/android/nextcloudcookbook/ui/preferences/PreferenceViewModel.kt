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
 * @version 1.0, 26.04.20
 */
class PreferenceViewModel(application: Application) : AndroidViewModel(application) {
   private val prefDao = PreferenceDao.getInstance(application)

   internal val recipeDirectory: SharedPreferenceLiveData<String>
   internal val descSorting: SharedPreferenceLiveData<Boolean>
   internal val theme: SharedPreferenceLiveData<Int>

   init {
      recipeDirectory = prefDao.getRecipeDirectory()
      descSorting = prefDao.getDescSorting()
      theme = prefDao.getTheme()
   }

   fun setRecipeDirectory(recipeDirectory: String) {
      prefDao.setRecipeDirectory(recipeDirectory)
   }

   fun setDescSorting(sort: Boolean) {
      prefDao.setDescSorting(sort)
   }

   fun setTheme(theme: Int) {
      prefDao.setTheme(theme)
   }
}
