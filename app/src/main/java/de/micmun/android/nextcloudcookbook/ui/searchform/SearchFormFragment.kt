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
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.micmun.android.nextcloudcookbook.MainApplication
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.CategoryFilter
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.databinding.FragmentSearchFormBinding
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModel
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModelFactory
import de.micmun.android.nextcloudcookbook.ui.MainActivity
import java.util.stream.Collectors

/**
 * Fragment for advanced search formular.
 *
 * @author MicMun
 * @version 1.4, 24.04.21
 */
class SearchFormFragment : Fragment(), SearchClickListener {
   private lateinit var binding: FragmentSearchFormBinding
   private lateinit var settingViewModel: CurrentSettingViewModel
   private lateinit var category: CategoryFilter
   private lateinit var searchFormViewModel: SearchFormViewModel

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_form, container, false)
      binding.clickListener = this

      val factory = CurrentSettingViewModelFactory(MainApplication.AppContext)
      settingViewModel =
         ViewModelProvider(MainApplication.AppContext, factory).get(CurrentSettingViewModel::class.java)

      settingViewModel.category.observe(viewLifecycleOwner, {
         category = it ?: CategoryFilter(CategoryFilter.CategoryFilterOption.ALL_CATEGORIES)
      })

      searchFormViewModel = ViewModelProvider(this).get(SearchFormViewModel::class.java)
      searchFormViewModel.keywords.observe(viewLifecycleOwner, Observer {
         it?.let { keywords ->
            val keywordsString = keywords.stream().map { k -> k.keyword }.collect(Collectors.toList())
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, keywordsString)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.searchKeyWord.adapter = adapter
         }
      })

      binding.searchTypes.setOnCheckedChangeListener { _, checkedId ->
         if (checkedId == R.id.typeKeyword) {
            binding.searchKeyWord.visibility = View.VISIBLE
            binding.searchTxt.visibility = View.GONE
         } else {
            binding.searchKeyWord.visibility = View.GONE
            binding.searchTxt.visibility = View.VISIBLE
            if (checkedId == R.id.typeYield) {
               binding.searchTxt.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_NORMAL
            } else {
               binding.searchTxt.inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_NORMAL
            }
         }
      }

      searchFormViewModel.searchType.observe(viewLifecycleOwner, {
         it?.let { binding.searchTypes.check(it) }
      })

      searchFormViewModel.currentKeyword.observe(viewLifecycleOwner, {
         it?.let { binding.searchKeyWord.setSelection(it) }
      })

      searchFormViewModel.currentQuery.observe(viewLifecycleOwner, {
         it?.let { binding.searchTxt.setText(it, TextView.BufferType.EDITABLE) }
      })

      searchFormViewModel.caseSensitive.observe(viewLifecycleOwner, {
         it?.let { binding.ignoreCaseChkBox.isChecked = it }
      })

      searchFormViewModel.exactSearch.observe(viewLifecycleOwner, {
         it?.let { binding.exactSearchChkBox.isChecked = it }
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

      val query = if (binding.searchTypes.checkedRadioButtonId == R.id.typeKeyword) {
         binding.searchKeyWord.selectedItem?.toString() ?: ""
      } else {
         binding.searchTxt.text.toString()
      }

      // no search text -> no search
      if (query.isEmpty())
         return

      val type = when (binding.searchTypes.checkedRadioButtonId) {
         R.id.typeKeyword -> RecipeFilter.QueryType.QUERY_KEYWORD
         R.id.typeIngredient -> RecipeFilter.QueryType.QUERY_INGREDIENTS
         R.id.typeYield -> RecipeFilter.QueryType.QUERY_YIELD
         else -> RecipeFilter.QueryType.QUERY_KEYWORD
      }
      val ignoreCase = binding.ignoreCaseChkBox.isChecked
      val exact = binding.exactSearchChkBox.isChecked

      val filter = RecipeFilter(type, query, ignoreCase, exact)

      // save search values
      when (type) {
         RecipeFilter.QueryType.QUERY_KEYWORD -> searchFormViewModel.setCurrentKeyword(
            binding.searchKeyWord.selectedItemPosition)
         else -> searchFormViewModel.setCurrentQuery(query)
      }
      searchFormViewModel.setSearchType(binding.searchTypes.checkedRadioButtonId)
      searchFormViewModel.setCaseSensitive(ignoreCase)
      searchFormViewModel.setExactSearch(exact)

      findNavController().navigate(SearchFormFragmentDirections.actionSearchFormFragmentToRecipeSearchFragment(filter))
   }
}

interface SearchClickListener {
   fun doSearch()
}
