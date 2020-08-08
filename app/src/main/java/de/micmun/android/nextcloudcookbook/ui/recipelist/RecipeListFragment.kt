package de.micmun.android.nextcloudcookbook.ui.recipelist

import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.postDelayed
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.CategoryFilter
import de.micmun.android.nextcloudcookbook.data.RecipeRepository
import de.micmun.android.nextcloudcookbook.data.SortValue
import de.micmun.android.nextcloudcookbook.databinding.FragmentRecipelistBinding
import de.micmun.android.nextcloudcookbook.ui.CurrentCategoryViewModel
import de.micmun.android.nextcloudcookbook.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_recipelist.*

/**
 * Fragment for list of recipes.
 *
 * @author MicMun
 * @version 1.9, 08.08.20
 */
class RecipeListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
   private lateinit var binding: FragmentRecipelistBinding
   private lateinit var recipesViewModel: RecipeListViewModel
   private lateinit var catViewModel: CurrentCategoryViewModel

   private var refreshItem: MenuItem? = null

   private var currentCatId: Int? = null

   private var sortDialog: AlertDialog? = null
   private var currentSort: SortValue? = null

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
         R.id.sortAction -> {
            showSortOptions()
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

      // settings
      adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

      recipesViewModel.navigateToRecipe.observe(viewLifecycleOwner, Observer { recipe ->
         recipe?.let {
            this.findNavController()
               .navigate(RecipeListFragmentDirections.actionRecipeListFragmentToRecipeDetailFragment(recipe))
            recipesViewModel.onRecipeNavigated()
         }
      })
      recipesViewModel.recipeDirectory.observe(viewLifecycleOwner, Observer { path ->
         recipesViewModel.setPath(path)
      })

      recipesViewModel.category.observe(viewLifecycleOwner, Observer { id ->
         id?.let {
            setCategoryFilter(id)
            recipesViewModel.onCategoryFiltered()
         }
      })

      recipesViewModel.sorting.observe(viewLifecycleOwner, Observer { s ->
         s?.let { sorting ->
            currentSort = SortValue.getByValue(sorting)
            recipesViewModel.sortList(currentSort!!)
         }
      })

      catViewModel.category.observe(viewLifecycleOwner, Observer { id ->
         currentCatId = id
         recipesViewModel.setFilterCategory(id)
      })

      recipesViewModel.path.observe(viewLifecycleOwner, Observer {
         it?.let {
            onRefresh()
         }
      })

      recipesViewModel.recipes.observe(viewLifecycleOwner, Observer {
         it?.let {
            adapter.submitList(it)
         }
         if (it.isNullOrEmpty() && R.id.emptyConstraint == binding.switcher.nextView.id) {
            binding.switcher.showNext()
         } else {
            if (R.id.titleConstraint == binding.switcher.nextView.id) {
               binding.switcher.showNext()
            }
         }
      })

      recipesViewModel.categoryFilter.observe(viewLifecycleOwner, Observer { catFilter ->
         catFilter?.let {
            // filter recipes to set categorie
            recipesViewModel.filterRecipesByCategory()
         }
      })
   }

   private fun setCategoryFilter(catId: Int) {
      var option = CategoryFilter.CategoryFilterOption.CATEGORY

      when (catId) {
         -1 -> {
            option = CategoryFilter.CategoryFilterOption.ALL_CATEGORIES
            // set title in Actionbar
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
         }
         R.id.menu_uncategorized -> {
            option = CategoryFilter.CategoryFilterOption.UNCATEGORIZED
            // set title in Actionbar
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.text_uncategorized)
         }
         else -> {
            (activity as AppCompatActivity).supportActionBar?.title =
               RecipeRepository.getInstance().getCategoryTitle(catId)
         }
      }
      recipesViewModel.setCategoryFilter(CategoryFilter(option, catId))
   }

   private fun showSortOptions() {
      val sortNames = resources.getStringArray(R.array.sort_names)
      val builder = AlertDialog.Builder(requireContext())
      builder.setTitle(R.string.menu_sort_title)
      val sortValue = currentSort ?: SortValue.NAME_A_Z
      builder.setSingleChoiceItems(sortNames, sortValue.sort) { _: DialogInterface, which: Int ->
         val sorting = SortValue.getByValue(which)
         recipesViewModel.setListSorting(sorting)
         sortDialog?.dismiss()
         sortDialog = null
         recipeList.postDelayed(200) {
            recipeList.smoothScrollToPosition(0)
         }
      }
      builder.setOnDismissListener { sortDialog = null }
      sortDialog = builder.show()
   }

   override fun onRefresh() {
      // preparation for loading
      binding.swipeContainer.isRefreshing = true
      refreshItem?.let { it.isEnabled = false }

      // load recipes and set categories in menu
      val activity = requireActivity() as MainActivity
      val menu = activity.getMenu()
      recipesViewModel.initRecipes(menu)
      currentCatId?.let { id -> recipesViewModel.setFilterCategory(id) }

      // end of loading
      binding.swipeContainer.isRefreshing = false
      refreshItem?.let { it.isEnabled = true }
   }

   override fun onPause() {
      sortDialog?.dismiss()
      super.onPause()
   }
}
