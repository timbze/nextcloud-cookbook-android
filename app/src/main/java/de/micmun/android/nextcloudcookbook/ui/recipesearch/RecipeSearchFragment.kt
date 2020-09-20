/*
 * RecipeSearchFragment.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipesearch

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.databinding.FragmentRecipesearchBinding
import de.micmun.android.nextcloudcookbook.ui.CurrentCategoryViewModel
import de.micmun.android.nextcloudcookbook.ui.MainActivity
import de.micmun.android.nextcloudcookbook.ui.recipelist.RecipeListAdapter
import de.micmun.android.nextcloudcookbook.ui.recipelist.RecipeListListener

/**
 * Fragment for search result.
 *
 * @author MicMun
 * @version 1.4, 20.09.20
 */
class RecipeSearchFragment : Fragment() {
   private lateinit var binding: FragmentRecipesearchBinding
   private lateinit var recipeSearchViewModel: RecipeSearchViewModel
   private lateinit var filter: RecipeFilter
   private lateinit var catViewModel: CurrentCategoryViewModel

   private var listState: Parcelable? = null

   companion object {
      private const val LIST_STATE = "listState"
   }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipesearch, container, false)

      val args = RecipeSearchFragmentArgs.fromBundle(requireArguments())
      filter = args.filter

      catViewModel = ViewModelProvider(MainActivity.mainApplication).get(CurrentCategoryViewModel::class.java)

      catViewModel.category.observe(viewLifecycleOwner, {
         initializeRecipeList(filter, it)
      })

      return binding.root
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      (requireActivity() as AppCompatActivity).supportActionBar?.title = filter.query
   }

   private fun initializeRecipeList(filter: RecipeFilter, catId: Int) {
      val viewModelFactory = RecipeSearchViewModelFactory(catId, filter, requireActivity().application)
      recipeSearchViewModel = ViewModelProvider(this, viewModelFactory).get(RecipeSearchViewModel::class.java)
      binding.recipeSearchViewModel = recipeSearchViewModel
      binding.lifecycleOwner = this

      // divider for recyclerview
      val dividerDecoration = DividerItemDecoration(binding.recipeResultList.context, LinearLayoutManager.VERTICAL)
      binding.recipeResultList.addItemDecoration(dividerDecoration)

      // data adapter
      val adapter =
         RecipeListAdapter(RecipeListListener { recipeId -> recipeSearchViewModel.onRecipeClicked(recipeId) })
      binding.recipeResultList.adapter = adapter
      adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

      // observe live data
      recipeSearchViewModel.recipeList.observe(viewLifecycleOwner, {
         it?.let {
            adapter.submitList(it)

            if (it.isNotEmpty()) {
               if (R.id.titleConstraint == binding.switcher2.nextView.id) {
                  binding.switcher2.showNext()
               }
            } else if (R.id.emptyConstraint == binding.switcher2.nextView.id) {
               binding.switcher2.showNext()
            }
         }
      })

      recipeSearchViewModel.navigateToRecipe.observe(viewLifecycleOwner, { recipe ->
         recipe?.let {
            this.findNavController()
               .navigate(RecipeSearchFragmentDirections.actionRecipeSearchFragmentToRecipeDetailFragment(recipe))
            recipeSearchViewModel.onRecipeNavigated()
         }
      })
   }

   override fun onSaveInstanceState(outState: Bundle) {
      super.onSaveInstanceState(outState)
      listState = binding.recipeResultList.layoutManager?.onSaveInstanceState()
      outState.putParcelable(LIST_STATE, listState)
   }

   override fun onViewStateRestored(savedInstanceState: Bundle?) {
      super.onViewStateRestored(savedInstanceState)

      if (savedInstanceState != null) {
         listState = savedInstanceState[LIST_STATE] as Parcelable?
      }
   }

   override fun onResume() {
      super.onResume()

      if (listState != null) {
         binding.recipeResultList.layoutManager?.onRestoreInstanceState(listState)
      }
   }
}
