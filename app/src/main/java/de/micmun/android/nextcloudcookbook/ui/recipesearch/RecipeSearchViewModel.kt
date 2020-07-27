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
import de.micmun.android.nextcloudcookbook.data.PreferenceDao
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.data.RecipeRepository
import de.micmun.android.nextcloudcookbook.data.SortValue
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.util.DateComparator
import de.micmun.android.nextcloudcookbook.util.NameComparator
import de.micmun.android.nextcloudcookbook.util.TotalTimeComparator

/**
 * ViewModel for recipe search result.
 *
 * @author MicMun
 * @version 1.2, 26.07.20
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

      _recipeList.value = sortList(filter.filter(list))
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

   private fun sortList(list: List<Recipe>): List<Recipe> {
      val sorting = PreferenceDao.getInstance(getApplication()).getSortSync()

      val comparator = when (SortValue.getByValue(sorting)) {
         SortValue.NAME_A_Z -> NameComparator(true)
         SortValue.NAME_Z_A -> NameComparator(false)
         SortValue.DATE_ASC -> DateComparator(true)
         SortValue.DATE_DESC -> DateComparator(false)
         SortValue.TOTAL_TIME_ASC -> TotalTimeComparator(true)
         SortValue.TOTAL_TIME_DESC -> TotalTimeComparator(false)
         else -> NameComparator(true)
      }
      return list.sortedWith(comparator)
   }
}
