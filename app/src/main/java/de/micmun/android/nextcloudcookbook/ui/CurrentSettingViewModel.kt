/*
 * CurrentSettingViewModel
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.micmun.android.nextcloudcookbook.data.CategoryFilter
import de.micmun.android.nextcloudcookbook.settings.PreferenceDao
import de.micmun.android.nextcloudcookbook.settings.SharedPreferenceLiveData

/**
 * LiveData of the settings.
 *
 * @author MicMun
 * @version 1.4, 28.08.21
 */
class CurrentSettingViewModel(application: Application) : AndroidViewModel(application) {
   private val prefDao = PreferenceDao.getInstance(application)
   val recipeDirectory: SharedPreferenceLiveData<String> = prefDao.getRecipeDirectory()
   val sorting: SharedPreferenceLiveData<Int> = prefDao.getSort()
   val storageAccessed: SharedPreferenceLiveData<Boolean> = prefDao.isStorageAccessed()

   // category
   private val _category = MutableLiveData<CategoryFilter>()
   val category: LiveData<CategoryFilter>
      get() = _category

   // category changed
   private val _categoryChanged = MutableLiveData<Boolean>()
   val categoryChanged: LiveData<Boolean>
      get() = _categoryChanged

   fun setSorting(sort: Int) {
      prefDao.setSort(sort)
   }

   fun setNewCategory(cat: CategoryFilter) {
      val changed = _category.value == cat
      _category.value = cat
      _categoryChanged.value = changed
   }

   fun resetCategoryChanged() {
      _categoryChanged.value = false
   }
}
