/*
 * RecipeSearchViewModelFactory.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipesearch

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for RecipeSearchViewModel.
 *
 * @author MicMun
 * @version 1.1, 12.07.20
 */
class RecipeSearchViewModelFactory(private val application: Application) :
   ViewModelProvider.Factory {
   override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(RecipeSearchViewModel::class.java)) {
         @Suppress("UNCHECKED_CAST")
         return RecipeSearchViewModel(application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
   }
}
