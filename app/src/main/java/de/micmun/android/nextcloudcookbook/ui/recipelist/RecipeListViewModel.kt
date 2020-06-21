/*
 * RecipeViewModel.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipelist

import android.app.Application
import android.os.Build
import android.text.Html
import android.view.Menu
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.PreferenceDao
import de.micmun.android.nextcloudcookbook.data.RecipeRepository
import de.micmun.android.nextcloudcookbook.data.SharedPreferenceLiveData
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import kotlinx.coroutines.*

/**
 * ViewModel for list of recipes.
 *
 * @author MicMun
 * @version 1.3, 21.06.20
 */
class RecipeListViewModel(application: Application) : AndroidViewModel(application) {
   private val _recipeList = MutableLiveData<List<Recipe>>()
   val recipeList: LiveData<List<Recipe>>
      get() = _recipeList

   private var viewModelJob = Job()
   private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

   val recipeDirectory: SharedPreferenceLiveData<String>

   init {
      val prefDao = PreferenceDao.getInstance(application)
      recipeDirectory = prefDao.getRecipeDirectory()
   }

   override fun onCleared() {
      super.onCleared()
      viewModelJob.cancel()
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

   private val _filterCategory = MutableLiveData<Int>()
   val filterCategory
      get() = _filterCategory

   fun onCategoryClicked(id: Int) {
      _filterCategory.value = id
   }

   fun filterRecipes(option: CategoryFilterOption) {
      val repo = RecipeRepository.getInstance()

      when (option) {
         CategoryFilterOption.ALL_CATEGORIES -> _recipeList.value = repo.recipeList
         CategoryFilterOption.UNCATEGORIZED -> _recipeList.value = repo.filterRecipesUncategorized()
         else -> _recipeList.value = repo.filterRecipesWithCategory(_filterCategory.value!!)
      }
   }

   fun initRecipes(path: String, menu: Menu, force: Boolean) {
      uiScope.launch {
         if (_recipeList.value.isNullOrEmpty() || force) {
            _recipeList.value = getRecipesFromRepo(path)

            _filterCategory.value = R.id.menu_all_categories
         }
         val categories = getCategoriesFromRepo()
         var order = 1

         menu.removeGroup(R.id.menu_categories_group)

         categories.forEach { category ->
            val title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
               Html.fromHtml(category, Html.FROM_HTML_MODE_LEGACY)
            else
               Html.fromHtml(category)
            menu.add(R.id.menu_categories_group, category.hashCode(), order++, title)
         }
      }
   }

   private suspend fun getRecipesFromRepo(path: String): List<Recipe> {
      return withContext(Dispatchers.IO) {
         RecipeRepository.getInstance().getAllRecipes(path)
      }
   }

   private suspend fun getCategoriesFromRepo(): Set<String> {
      return withContext(Dispatchers.IO) {
         RecipeRepository.getInstance().recipeCategories
      }
   }

   enum class CategoryFilterOption {
      ALL_CATEGORIES, UNCATEGORIZED, CATEGORY
   }
}
