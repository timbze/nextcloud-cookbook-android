/*
 * CurrentCategoryViewModel.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Holds the selected category for all fragments.
 *
 * @author MicMun
 * @version 1.0, 12.07.20
 */
class CurrentCategoryViewModel : ViewModel() {
   private val _category = MutableLiveData<Int>()
   val category: LiveData<Int>
      get() = _category

   init {
      _category.value = -1
   }

   fun setCategory(id: Int) {
      _category.value = id
   }
}
