package de.micmun.android.nextcloudcookbook.ui.recipelist

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.RecipeRepository
import de.micmun.android.nextcloudcookbook.databinding.FragmentRecipelistBinding
import de.micmun.android.nextcloudcookbook.ui.CurrentCategoryViewModel
import de.micmun.android.nextcloudcookbook.ui.MainActivity

/**
 * Fragment for list of recipes.
 *
 * @author MicMun
 * @version 1.6, 13.07.20
 */
class RecipeListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
   private lateinit var binding: FragmentRecipelistBinding
   private lateinit var recipesViewModel: RecipeListViewModel
   private lateinit var catViewModel: CurrentCategoryViewModel

   private var recipePath: String? = null
   private var refreshItem: MenuItem? = null

   private var forceRefresh = true
   private var currentCatId: Int? = null

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipelist, container, false)
      setHasOptionsMenu(true)

      binding.swipeContainer.setOnRefreshListener(this)
      recipesViewModel = ViewModelProvider(this).get(RecipeListViewModel::class.java)
      catViewModel = ViewModelProvider(MainActivity.mainApplication).get(CurrentCategoryViewModel::class.java)

      initializeRecipeList()

      return binding.root
   }

   override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
      super.onCreateOptionsMenu(menu, inflater)
      inflater.inflate(R.menu.overflow_menu, menu)

      // Associate searchable configuration with the SearchView
      val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
      val searchView = menu.findItem(R.id.search).actionView as SearchView
      searchView.apply {
         setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
      }
   }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return when (item.itemId) {
         R.id.refreshAction -> {
            refreshItem = item
            onRefresh()
            true
         }
         else -> NavigationUI
                    .onNavDestinationSelected(item,
                                              requireView().findNavController()) || super.onOptionsItemSelected(item)
      }
   }

   private fun initializeRecipeList() {
      binding.recipeListViewModel = recipesViewModel
      binding.lifecycleOwner = this

      // divider for recyclerview
      val dividerDecoration = DividerItemDecoration(binding.recipeList.context, LinearLayoutManager.VERTICAL)
      binding.recipeList.addItemDecoration(dividerDecoration)

      // data adapter
      val adapter = RecipeListAdapter(RecipeListListener { recipeId -> recipesViewModel.onRecipeClicked(recipeId) })
      binding.recipeList.adapter = adapter

      recipesViewModel.recipeList.observe(viewLifecycleOwner, Observer {
         it?.let {
            adapter.setRecipes(it)

            if (it.isNotEmpty()) {
               if (R.id.titleConstraint == binding.switcher.nextView.id) {
                  binding.switcher.showNext()
               }
            } else if (R.id.emptyConstraint == binding.switcher.nextView.id) {
               binding.switcher.showNext()
            }
         }
      })
      recipesViewModel.navigateToRecipe.observe(viewLifecycleOwner, Observer { recipe ->
         recipe?.let {
            this.findNavController()
               .navigate(RecipeListFragmentDirections.actionRecipeListFragmentToRecipeDetailFragment(recipe))
            recipesViewModel.onRecipeNavigated()
         }
      })
      recipesViewModel.recipeDirectory.observe(viewLifecycleOwner, Observer { path ->
         recipePath = path
         forceRefresh = false
         onRefresh()
         forceRefresh = true
      })

      recipesViewModel.category.observe(viewLifecycleOwner, Observer { id ->
         id?.let {
            setCategoryFilter(id)
            recipesViewModel.onCategoryFiltered()
         }

      })

      catViewModel.category.observe(viewLifecycleOwner, Observer { id ->
         currentCatId = id
         recipesViewModel.setFilterCategory(id)
      })
   }

   private fun setCategoryFilter(catId: Int) {
      var option = RecipeListViewModel.CategoryFilterOption.CATEGORY

      when (catId) {
         -1 -> {
            option = RecipeListViewModel.CategoryFilterOption.ALL_CATEGORIES
            // set title in Actionbar
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
         }
         R.id.menu_uncategorized -> {
            option = RecipeListViewModel.CategoryFilterOption.UNCATEGORIZED
            // set title in Actionbar
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.text_uncategorized)
         }
         else -> {
            (activity as AppCompatActivity).supportActionBar?.title =
               RecipeRepository.getInstance().getCategoryTitle(catId)
         }
      }
      // filter recipes to set categorie
      recipesViewModel.filterRecipesByCategory(option, catId)
   }

   override fun onRefresh() {
      // preparation for loading
      binding.swipeContainer.isRefreshing = true
      refreshItem?.let { it.isEnabled = false }

      // load recipes and set categories in menu
      val activity = requireActivity() as MainActivity
      val menu = activity.getMenu()
      recipePath?.let {
         recipesViewModel.initRecipes(it, menu, forceRefresh)
         currentCatId?.let { id -> recipesViewModel.setFilterCategory(id) }
      }

      // end of loading
      binding.swipeContainer.isRefreshing = false
      refreshItem?.let { it.isEnabled = true }
   }
}
