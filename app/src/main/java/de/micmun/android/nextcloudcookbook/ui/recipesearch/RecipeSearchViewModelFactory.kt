/*
 * RecipeSearchViewModelFactory.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipesearch

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.ui.recipedetail.RecipeViewModel

/**
 * Factory for RecipeSearchViewModel.
 *
 * @author MicMun
 * @version 1.1, 12.07.20
 */
class RecipeSearchViewModelFactory(private val categoryId: Int, private val filter: RecipeFilter,
                                   private val application: Application): ViewModelProvider.Factory {
   override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(RecipeSearchViewModel::class.java)) {
         @Suppress("UNCHECKED_CAST")
         return RecipeSearchViewModel(categoryId, filter, application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
   }
}
