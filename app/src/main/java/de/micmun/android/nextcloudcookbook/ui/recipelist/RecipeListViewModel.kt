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
import de.micmun.android.nextcloudcookbook.data.SortValue
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.util.DateComparator
import de.micmun.android.nextcloudcookbook.util.NameComparator
import de.micmun.android.nextcloudcookbook.util.TotalTimeComparator
import kotlinx.coroutines.*

/**
 * ViewModel for list of recipes.
 *
 * @author MicMun
 * @version 1.6, 25.07.20
 */
class RecipeListViewModel(application: Application) : AndroidViewModel(application) {
   private val _recipeList = MutableLiveData<List<Recipe>>()
   val recipeList: LiveData<List<Recipe>>
      get() = _recipeList

   private var viewModelJob = Job()
   private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

   private val prefDao: PreferenceDao = PreferenceDao.getInstance(application)
   val recipeDirectory: SharedPreferenceLiveData<String>
   val sorting: SharedPreferenceLiveData<Int>
   private var option: CategoryFilterOption? = null
   private var catId: Int? = null

   init {
      recipeDirectory = prefDao.getRecipeDirectory()
      sorting = prefDao.getSort()
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

   private val _category = MutableLiveData<Int>()
   val category
      get() = _category

   fun setFilterCategory(id: Int) {
      _category.value = id
   }

   fun onCategoryFiltered() {
      _category.value = null
   }

   fun filterRecipesByCategory(option: CategoryFilterOption, catId: Int) {
      val repo = RecipeRepository.getInstance()

      when (option) {
         CategoryFilterOption.ALL_CATEGORIES -> _recipeList.value = repo.recipeList
         CategoryFilterOption.UNCATEGORIZED -> _recipeList.value = repo.filterRecipesUncategorized()
         else -> _recipeList.value = repo.filterRecipesWithCategory(catId)
      }
      this.option = option
      this.catId = catId

      val sort = SortValue.getByValue(PreferenceDao.getInstance(getApplication()).getSortSync())
      sort?.let { sortRecipeList(it) }
   }

   fun initRecipes(path: String, menu: Menu, force: Boolean = true) {
      uiScope.launch {
         if (_recipeList.value.isNullOrEmpty() || force) {
            _recipeList.value = getRecipesFromRepo(path)
         }
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
         option?.let { filterRecipesByCategory(it, catId!!) }
      }
   }

   fun setListSorting(sort: SortValue) {
      prefDao.setSort(sort.sort)
   }

   fun sortList(sort: SortValue) {
      sortRecipeList(sort)
   }

   private fun sortRecipeList(sort: SortValue) {
      val comparator = when (sort) {
         SortValue.NAME_A_Z -> NameComparator(true)
         SortValue.NAME_Z_A -> NameComparator(false)
         SortValue.DATE_ASC -> DateComparator(true)
         SortValue.DATE_DESC -> DateComparator(false)
         SortValue.TOTAL_TIME_ASC -> TotalTimeComparator(true)
         SortValue.TOTAL_TIME_DESC -> TotalTimeComparator(false)
      }
      _recipeList.value = _recipeList.value?.sortedWith(comparator)
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
