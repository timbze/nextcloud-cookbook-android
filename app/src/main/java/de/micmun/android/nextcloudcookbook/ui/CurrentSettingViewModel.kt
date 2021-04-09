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
import de.micmun.android.nextcloudcookbook.settings.PreferenceDao
import de.micmun.android.nextcloudcookbook.settings.SharedPreferenceLiveData
import de.micmun.android.nextcloudcookbook.data.CategoryFilter

/**
 * LiveData of the settings.
 *
 * @author MicMun
 * @version 1.0, 26.01.21
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

   fun setSorting(sort: Int) {
      prefDao.setSort(sort)
   }

   fun setNewCategory(cat: CategoryFilter) {
      _category.value = cat
   }
}
