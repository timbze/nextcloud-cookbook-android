/*
 * 
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.micmun.android.nextcloudcookbook.db.RecipeDataDao

/**
 * Factory to create RecipeViewModel.
 *
 * @author MicMun
 * @version 1.1, 21.03.21
 */

class RecipeViewModelFactory(private val name: String, private val application: Application)
   : ViewModelProvider.Factory {
   override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
         @Suppress("UNCHECKED_CAST")
         return RecipeViewModel(name, application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
   }
}
