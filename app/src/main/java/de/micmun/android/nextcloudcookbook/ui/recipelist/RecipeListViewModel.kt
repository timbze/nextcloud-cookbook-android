/*
 * RecipeViewModel.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipelist

import android.app.Application
import androidx.lifecycle.*
import de.micmun.android.nextcloudcookbook.data.CategoryFilter
import de.micmun.android.nextcloudcookbook.data.SortValue
import de.micmun.android.nextcloudcookbook.db.DbRecipeRepository
import de.micmun.android.nextcloudcookbook.db.model.DbRecipe
import de.micmun.android.nextcloudcookbook.json.JsonRecipeRepository
import de.micmun.android.nextcloudcookbook.json.model.Recipe
import de.micmun.android.nextcloudcookbook.util.Recipe2DbRecipeConverter
import kotlinx.coroutines.*
import java.util.stream.Collectors

/**
 * ViewModel for list of recipes.
 *
 * @author MicMun
 * @version 1.8, 26.01.21
 */
class RecipeListViewModel(private val app: Application) : AndroidViewModel(app) {
   // coroutines
   private var viewModelJob = Job()

   private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

   private val recipeRepository = DbRecipeRepository.getInstance(app)
   val categories = recipeRepository.getCategories()

   // on updating
   val isUpdating = MutableLiveData(false)
   val isLoaded = MutableLiveData(false)

   private var recipeDir: String = ""

   // sorting and category
   private var sort: SortValue = SortValue.NAME_A_Z
   private var catFilter: CategoryFilter = CategoryFilter(CategoryFilter.CategoryFilterOption.ALL_CATEGORIES)

   // navigate to recipe
   private val _navigateToRecipe = MutableLiveData<String>()
   val navigateToRecipe
      get() = _navigateToRecipe

   fun onRecipeClicked(name: String) {
      _navigateToRecipe.value = name
   }

   fun onRecipeNavigated() {
      _navigateToRecipe.value = null
   }

   fun getRecipes(): LiveData<List<DbRecipe>> {
      var recipes: LiveData<List<DbRecipe>>

      runBlocking(Dispatchers.IO) {
         recipes =
            if (catFilter.type == CategoryFilter.CategoryFilterOption.ALL_CATEGORIES && sort == SortValue.NAME_A_Z) {
               recipeRepository.getAllRecipes()
            } else if (catFilter.type == CategoryFilter.CategoryFilterOption.ALL_CATEGORIES) {
               recipeRepository.sort(sort)
            } else if (catFilter.type == CategoryFilter.CategoryFilterOption.UNCATEGORIZED) {
               recipeRepository.filterUncategorized(sort)
            } else {
               recipeRepository.filterCategory(sort, catFilter.name)
            }
      }
      return recipes
   }

   // read recipes
   fun initRecipes(path: String = "") {
      if (path.isNotEmpty()) {
         recipeDir = path
      }
      val dir = if (path.isEmpty()) recipeDir else path
      if (dir.isEmpty())
         return

      isUpdating.postValue(true)

      uiScope.launch {
         val list = getRecipesFromRepo(dir)
         val dbList = list.stream()
            .map { Recipe2DbRecipeConverter(it).convert() }
            .collect(Collectors.toList())
         recipeRepository.insertAll(dbList)

         isLoaded.postValue(true)
         isUpdating.postValue(false)
      }
   }

   private suspend fun getRecipesFromRepo(path: String): List<Recipe> {
      return withContext(Dispatchers.IO) {
         JsonRecipeRepository.getInstance().getAllRecipes(app, path)
      }
   }

   // category filter
   fun filterRecipesByCategory(catFilter: CategoryFilter) {
      this.catFilter = catFilter
   }

   fun sortList(sort: SortValue) {
      this.sort = sort
   }

   override fun onCleared() {
      super.onCleared()
      viewModelJob.cancel()
   }
}

class RecipeListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
   @Suppress("UNCHECKED_CAST")
   override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(RecipeListViewModel::class.java)) {
         return RecipeListViewModel(application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
   }
}
