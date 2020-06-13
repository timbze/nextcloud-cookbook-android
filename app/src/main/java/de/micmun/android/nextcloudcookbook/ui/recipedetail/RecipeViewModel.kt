/*
 * RecipeViewModel.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.micmun.android.nextcloudcookbook.data.RecipeRepository
import de.micmun.android.nextcloudcookbook.data.model.Recipe

/**
 * ViewModel for one Recipe.
 *
 * @author MicMun
 * @version 1.0, 08.03.20
 */
class RecipeViewModel(recipeId: Long, application: Application) : AndroidViewModel(application) {
   private val _recipe = MutableLiveData<Recipe>()
   val recipe: LiveData<Recipe>
      get() = _recipe

   init {
      val repository = RecipeRepository.getInstance()
      _recipe.value = repository.getRecipeWithId(recipeId)
   }
}
