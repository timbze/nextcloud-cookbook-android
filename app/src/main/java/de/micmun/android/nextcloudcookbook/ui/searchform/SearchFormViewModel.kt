/*
 * SearchFormViewModel.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.searchform

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.db.DbRecipeRepository

/**
 * ViewModel for search formular.
 *
 * @author MicMun
 * @version 1.0, 24.04.21
 */
class SearchFormViewModel(application: Application) : AndroidViewModel(application) {
   private val repository = DbRecipeRepository.getInstance(application)
   val keywords = repository.getKeywords()

   private val _searchType = MutableLiveData(R.id.typeKeyword)
   val searchType: LiveData<Int>
      get() = _searchType
   private val _caseSensitive = MutableLiveData(true)
   val caseSensitive: LiveData<Boolean>
      get() = _caseSensitive
   private val _exactSearch = MutableLiveData(false)
   val exactSearch: LiveData<Boolean>
      get() = _exactSearch

   private val _currentQuery = MutableLiveData("")
   val currentQuery: LiveData<String>
      get() = _currentQuery

   private val _currentKeyword = MutableLiveData(0)
   val currentKeyword: LiveData<Int>
      get() = _currentKeyword

   fun setSearchType(type: Int) {
      _searchType.value = type
   }

   fun setCaseSensitive(case: Boolean) {
      _caseSensitive.value = case
   }

   fun setExactSearch(exact: Boolean) {
      _exactSearch.value = exact
   }

   fun setCurrentQuery(query: String) {
      _currentQuery.value = query
   }

   fun setCurrentKeyword(keywordPos: Int) {
      _currentKeyword.value = keywordPos
   }
}
