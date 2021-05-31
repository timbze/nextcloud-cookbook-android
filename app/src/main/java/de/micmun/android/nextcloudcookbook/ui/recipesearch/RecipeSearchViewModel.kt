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
import de.micmun.android.nextcloudcookbook.data.CategoryFilter
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.data.SortValue
import de.micmun.android.nextcloudcookbook.db.DbRecipeRepository
import de.micmun.android.nextcloudcookbook.db.model.DbRecipePreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * ViewModel for recipe search result.
 *
 * @author MicMun
 * @version 1.4, 24.04.21
 */
class RecipeSearchViewModel(application: Application) :
   AndroidViewModel(application) {
   private val recipeRepository = DbRecipeRepository.getInstance(application)

   private var sorting = SortValue.NAME_A_Z
   private var categoryFilter = CategoryFilter(CategoryFilter.CategoryFilterOption.ALL_CATEGORIES)
   private var applyFilter: RecipeFilter? = null
   private val _filter = MutableLiveData<RecipeFilter>()
   val filter: LiveData<RecipeFilter>
      get() = _filter

   fun getRecipes(): LiveData<List<DbRecipePreview>> {
      var recipes: LiveData<List<DbRecipePreview>>

      runBlocking(Dispatchers.IO) {
         recipes =
            if (categoryFilter.type == CategoryFilter.CategoryFilterOption.ALL_CATEGORIES && applyFilter != null) {
               recipeRepository.filterAll(sorting, applyFilter!!)
            } else if (categoryFilter.type == CategoryFilter.CategoryFilterOption.ALL_CATEGORIES) {
               recipeRepository.sort(sorting)
            } else if (categoryFilter.type == CategoryFilter.CategoryFilterOption.UNCATEGORIZED) {
               recipeRepository.filterUncategorized(sorting, applyFilter)
            } else {
               recipeRepository.filterCategory(sorting, categoryFilter.name, applyFilter)
            }
      }

      return recipes
   }

   fun setFilter(filter: RecipeFilter) {
      _filter.value = filter
   }

   fun setCategory(category: CategoryFilter) {
      this.categoryFilter = category
   }

   fun setSort(sortValue: SortValue) {
      sorting = sortValue
   }

   fun setSearchResult(filter: RecipeFilter) {
      applyFilter = filter
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
