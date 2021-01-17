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
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.*
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.util.DateComparator
import de.micmun.android.nextcloudcookbook.util.NameComparator
import de.micmun.android.nextcloudcookbook.util.TotalTimeComparator
import kotlinx.coroutines.*

/**
 * ViewModel for list of recipes.
 *
 * @author MicMun
 * @version 1.7, 08.08.20
 */
class RecipeListViewModel(application: Application) : AndroidViewModel(application) {
   // read from storage
   private val _recipeListUnsorted = MutableLiveData<List<Recipe>>()

   // coroutines
   private var viewModelJob = Job()
   private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

   // preferences
   private val prefDao: PreferenceDao = PreferenceDao.getInstance(application)
   val recipeDirectory: SharedPreferenceLiveData<String>
   val sorting: SharedPreferenceLiveData<Int>

   // live changed
   private val _sortValue = MutableLiveData<SortValue>()
   val sortValue: LiveData<SortValue>
      get() = _sortValue
   private val _path = MutableLiveData<String>()
   val path: LiveData<String>
      get() = _path
   private val _categoryFilter = MutableLiveData<CategoryFilter?>()
   val categoryFilter: LiveData<CategoryFilter?>
      get() = _categoryFilter

   // calculated
   internal val recipes: LiveData<List<Recipe>>

   init {
      recipeDirectory = prefDao.getRecipeDirectory()
      sorting = prefDao.getSort()
      recipes = SortLiveData()
   }

   // navigate to selected recipe
   private val _navigateToRecipe = MutableLiveData<Long>()
   val navigateToRecipe
      get() = _navigateToRecipe

   fun onRecipeClicked(id: Long) {
      _navigateToRecipe.value = id
   }

   fun onRecipeNavigated() {
      _navigateToRecipe.value = null
   }

   // read recipes
   fun initRecipes(menu: Menu) {
      uiScope.launch {
         path.value?.let {
            _recipeListUnsorted.value = getRecipesFromRepo(it)

            // categories in menu
            val categories = getCategoriesFromRepo()
            var order = 1

            menu.removeGroup(R.id.menu_categories_group)

            categories.forEach { category ->
               @Suppress("DEPRECATION") val title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                  Html.fromHtml(category, Html.FROM_HTML_MODE_LEGACY)
               else
                  Html.fromHtml(category)
               menu.add(R.id.menu_categories_group, category.hashCode(), order++, title)
            }
         }
      }
   }

   private suspend fun getRecipesFromRepo(path: String): List<Recipe> {
      return withContext(Dispatchers.IO) {
         RecipeRepository.getInstance().getAllRecipes(getApplication(), path)
      }
   }

   private suspend fun getCategoriesFromRepo(): Set<String> {
      return withContext(Dispatchers.IO) {
         RecipeRepository.getInstance().recipeCategories
      }
   }

   // sets the path
   fun setPath(path: String) {
      _path.value = path
   }

   // category filter
   private val _category = MutableLiveData<Int>()
   val category
      get() = _category

   fun setFilterCategory(catId: Int) {
      _category.value = catId
   }

   fun onCategoryFiltered() {
      _category.value = null
   }

   fun setCategoryFilter(catFilter: CategoryFilter) {
      _categoryFilter.value = catFilter
   }

   fun filterRecipesByCategory() {
      val repo = RecipeRepository.getInstance()

      when (categoryFilter.value!!.type) {
         CategoryFilter.CategoryFilterOption.ALL_CATEGORIES -> _recipeListUnsorted.value = repo.recipeList
         CategoryFilter.CategoryFilterOption.UNCATEGORIZED -> _recipeListUnsorted.value =
            repo.filterRecipesUncategorized()
         else -> _recipeListUnsorted.value = repo.filterRecipesWithCategory(categoryFilter.value!!.categoryId)
      }
   }

   // sorting
   internal inner class SortLiveData : MediatorLiveData<List<Recipe>>() {
      init {
         addSource(_recipeListUnsorted) {
            getRecipes()
         }
         addSource(sortValue) {
            getRecipes()
         }
      }

      private fun getRecipes() {
         value = _recipeListUnsorted.value?.sortedWith(getSortComparator(sortValue.value))
      }
   }

   fun setListSorting(sort: SortValue) {
      prefDao.setSort(sort.sort)
   }

   fun sortList(sort: SortValue) {
      _sortValue.value = sort
   }

   private fun getSortComparator(sort: SortValue?): Comparator<Recipe> {
      return when (sort) {
         SortValue.NAME_A_Z -> NameComparator(true)
         SortValue.NAME_Z_A -> NameComparator(false)
         SortValue.DATE_ASC -> DateComparator(true)
         SortValue.DATE_DESC -> DateComparator(false)
         SortValue.TOTAL_TIME_ASC -> TotalTimeComparator(true)
         SortValue.TOTAL_TIME_DESC -> TotalTimeComparator(false)
         else -> NameComparator(true)
      }
   }

   override fun onCleared() {
      super.onCleared()
      viewModelJob.cancel()
   }
}
