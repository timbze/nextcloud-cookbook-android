/*
 * SearchFormFragment.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.searchform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.databinding.FragmentSearchFormBinding
import de.micmun.android.nextcloudcookbook.ui.CurrentCategoryViewModel
import de.micmun.android.nextcloudcookbook.ui.MainActivity

/**
 * Fragment for advanced search formular.
 *
 * @author MicMun
 * @version 1.0, 12.07.20
 */
class SearchFormFragment : Fragment(), SearchClickListener {
   private lateinit var binding: FragmentSearchFormBinding
   private lateinit var catViewModel: CurrentCategoryViewModel
   private var categoryId = -1

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_form, container, false)
      binding.clickListener = this

      catViewModel = ViewModelProvider(MainActivity.mainApplication).get(CurrentCategoryViewModel::class.java)

      catViewModel.category.observe(viewLifecycleOwner, Observer {
         categoryId = it
      })

      return binding.root
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      (requireActivity() as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.form_search_title)
   }

   override fun doSearch() {
      /* hide keyboard */
      binding.searchTxt.onEditorAction(EditorInfo.IME_ACTION_DONE)

      val query = binding.searchTxt.text.toString()
      // no search text -> no search
      if (query.isEmpty())
         return

      val type = when (binding.searchTypes.checkedRadioButtonId) {
         R.id.typeKeyword -> RecipeFilter.QueryType.QUERY_KEYWORD
         R.id.typeIngredient -> RecipeFilter.QueryType.QUERY_INGREDIENTS
         else -> RecipeFilter.QueryType.QUERY_KEYWORD
      }
      val ignoreCase = binding.ignoreCaseChkBox.isChecked
      val exact = binding.exactSearchChkBox.isChecked

      val filter = RecipeFilter(type, query, ignoreCase, exact)
      findNavController().navigate(SearchFormFragmentDirections.actionSearchFormFragmentToRecipeSearchFragment(filter))
   }
}

interface SearchClickListener {
   fun doSearch()
}