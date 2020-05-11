/*
 * RecipeViewModel.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipelist

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.micmun.android.nextcloudcookbook.data.PreferenceDao
import de.micmun.android.nextcloudcookbook.data.RecipeRepository
import de.micmun.android.nextcloudcookbook.data.SharedPreferenceLiveData
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import kotlinx.coroutines.*

/**
 * ViewModel for list of recipes.
 *
 * @author MicMun
 * @version 1.0, 07.03.20
 */
class RecipeListViewModel(application: Application) : AndroidViewModel(application) {
   private val _recipeList = MutableLiveData<List<Recipe>>()
   val recipeList: LiveData<List<Recipe>>
      get() = _recipeList

   private var viewModelJob = Job()
   private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

   val recipeDirectory: SharedPreferenceLiveData<String>
   val descSorting: SharedPreferenceLiveData<Boolean>

   init {
      Log.i("RecipeViewModel", "RecipeViewModel created")
      val prefDao = PreferenceDao.getInstance(application)
      recipeDirectory = prefDao.getRecipeDirectory()
      descSorting = prefDao.getDescSorting()
   }

   override fun onCleared() {
      super.onCleared()
      viewModelJob.cancel()
      Log.i("RecipeViewModel", "RecipeViewModel cleared")
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

   fun initRecipes(path: String) {
      uiScope.launch {
         _recipeList.value = getRecipesFromRepo(path)
      }
   }

   fun sortRecipes(desc: Boolean) {
      if (desc) {
         _recipeList.value?.sortedByDescending { recipe -> recipe.name }
      } else {
         _recipeList.value?.sortedBy { recipe -> recipe.name }
      }
   }

   private suspend fun getRecipesFromRepo(path: String): List<Recipe> {
      return withContext(Dispatchers.IO) {
         RecipeRepository.getInstance().getAllRecipes(path)
      }
   }
}
