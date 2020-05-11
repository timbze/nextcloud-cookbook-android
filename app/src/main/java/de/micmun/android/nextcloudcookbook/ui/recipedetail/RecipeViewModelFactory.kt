/*
 * 
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory to create RecipeViewModel.
 *
 * @author MicMun
 * @version 1.0, 08.03.20
 */

class RecipeViewModelFactory(private val id: Long, private val application: Application)
   : ViewModelProvider.Factory {
   override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
         return RecipeViewModel(id, application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
   }
}
