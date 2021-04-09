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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.databinding.FragmentSearchFormBinding
import de.micmun.android.nextcloudcookbook.data.CategoryFilter
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModel
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModelFactory
import de.micmun.android.nextcloudcookbook.ui.MainActivity

/**
 * Fragment for advanced search formular.
 *
 * @author MicMun
 * @version 1.2, 07.04.21
 */
class SearchFormFragment : Fragment(), SearchClickListener {
   private lateinit var binding: FragmentSearchFormBinding
   private lateinit var settingViewModel: CurrentSettingViewModel
   private lateinit var category: CategoryFilter

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_form, container, false)
      binding.clickListener = this

      val factory = CurrentSettingViewModelFactory(MainActivity.mainApplication)
      settingViewModel =
         ViewModelProvider(MainActivity.mainApplication, factory).get(CurrentSettingViewModel::class.java)

      settingViewModel.category.observe(viewLifecycleOwner, {
         category = it ?: CategoryFilter(CategoryFilter.CategoryFilterOption.ALL_CATEGORIES)
      })

      binding.searchTypes.setOnCheckedChangeListener { _, checkedId ->
         if (checkedId == R.id.typeYield) {
            binding.searchTxt.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_NORMAL
         } else {
            binding.searchTxt.inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_NORMAL
         }
      }

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
         R.id.typeYield -> RecipeFilter.QueryType.QUERY_YIELD
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
