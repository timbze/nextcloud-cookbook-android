/*
 * RecipeSearchFragment.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipesearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.databinding.FragmentRecipesearchBinding
import de.micmun.android.nextcloudcookbook.ui.recipelist.RecipeListAdapter
import de.micmun.android.nextcloudcookbook.ui.recipelist.RecipeListListener

/**
 * Fragment for search result.
 *
 * @author MicMun
 * @version 1.0, 22.06.20
 */
class RecipeSearchFragment : Fragment() {
   private lateinit var binding: FragmentRecipesearchBinding
   private lateinit var viewModel: RecipeSearchViewModel
   private lateinit var query: String

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipesearch, container, false)
      setHasOptionsMenu(true)

      val args = RecipeSearchFragmentArgs.fromBundle(requireArguments())
      query = args.queryString
      initializeRecipeList(query, args.categoryCode)

      return binding.root
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      (requireActivity() as AppCompatActivity).supportActionBar?.title = query
   }

   private fun initializeRecipeList(q: String, catId: Int) {
      val viewModelFactory = RecipeSearchViewModelFactory(catId, q, requireActivity().application)
      viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeSearchViewModel::class.java)
      binding.recipeSearchViewModel = viewModel
      binding.lifecycleOwner = this

      // divider for recyclerview
      val dividerDecoration = DividerItemDecoration(binding.recipeResultList.context, LinearLayoutManager.VERTICAL)
      binding.recipeResultList.addItemDecoration(dividerDecoration)

      // data adapter
      val adapter = RecipeListAdapter(RecipeListListener { recipeId -> viewModel.onRecipeClicked(recipeId) })
      binding.recipeResultList.adapter = adapter

      // observe live data
      viewModel.recipeList.observe(viewLifecycleOwner, Observer {
         it?.let {
            adapter.setRecipes(it)

            if (it.isNotEmpty()) {
               if (R.id.titleConstraint == binding.switcher2.nextView.id) {
                  binding.switcher2.showNext()
               }
            } else if (R.id.emptyConstraint == binding.switcher2.nextView.id) {
               binding.switcher2.showNext()
            }
         }
      })
      viewModel.navigateToRecipe.observe(viewLifecycleOwner, Observer { recipe ->
         recipe?.let {
            this.findNavController()
               .navigate(RecipeSearchFragmentDirections.actionRecipeSearchFragmentToRecipeDetailFragment(recipe))
            viewModel.onRecipeNavigated()
         }
      })
   }
}
