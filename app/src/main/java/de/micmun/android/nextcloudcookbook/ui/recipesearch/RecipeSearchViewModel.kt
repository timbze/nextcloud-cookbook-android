/*
 * RecipeSearchViewModel.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipesearch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.data.RecipeRepository
import de.micmun.android.nextcloudcookbook.data.model.Recipe

/**
 * ViewModel for recipe search result.
 *
 * @author MicMun
 * @version 1.1, 12.07.20
 */
class RecipeSearchViewModel(categoryId: Int, filter: RecipeFilter, application: Application) :
   AndroidViewModel(application) {
   private val _recipeList = MutableLiveData<List<Recipe>>()
   val recipeList: LiveData<List<Recipe>>
      get() = _recipeList

   private var recipeRepository: RecipeRepository = RecipeRepository.getInstance()

   init {
      var list = recipeRepository.recipeList

      if (categoryId == R.id.menu_uncategorized) {
         list = recipeRepository.filterRecipesUncategorized()
      } else if (categoryId != -1) {
         list = recipeRepository.filterRecipesWithCategory(categoryId)
      }

      _recipeList.value = filter.filter(list)
   }

   private val _navigateToRecipe = MutableLiveData<Long>()
   val navigateToRecipe
      get() = _navigateToRecipe

   fun onRecipeClicked(id: Long) {
      _navigateToRecipe.value = id
   }

   fun onRecipeNavigated() {
      _navigateToRecipe.value = null
   }
}
