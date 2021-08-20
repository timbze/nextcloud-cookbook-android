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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.micmun.android.nextcloudcookbook.MainApplication
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.CategoryFilter
import de.micmun.android.nextcloudcookbook.data.SortValue
import de.micmun.android.nextcloudcookbook.databinding.FragmentRecipelistBinding
import de.micmun.android.nextcloudcookbook.db.DbRecipeRepository
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModel
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModelFactory
import de.micmun.android.nextcloudcookbook.ui.MainActivity

/**
 * Fragment for list of recipes.
 *
 * @author MicMun
 * @version 2.4, 31.07.21
 */
class RecipeListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
   private lateinit var binding: FragmentRecipelistBinding
   private lateinit var recipesViewModel: RecipeListViewModel
   private lateinit var settingViewModel: CurrentSettingViewModel
   private lateinit var adapter: RecipeListAdapter

   private var refreshItem: MenuItem? = null

   private var sortDialog: AlertDialog? = null
   private var currentSort: SortValue? = null

   private var isLoaded: Boolean = false

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipelist, container, false)
      setHasOptionsMenu(true)

      binding.swipeContainer.setOnRefreshListener(this)
      val recipeListViewModelFactory = RecipeListViewModelFactory(requireActivity().application)
      recipesViewModel = ViewModelProvider(this, recipeListViewModelFactory).get(RecipeListViewModel::class.java)
      val factory = CurrentSettingViewModelFactory(MainApplication.AppContext)
      settingViewModel =
         ViewModelProvider(MainApplication.AppContext, factory).get(CurrentSettingViewModel::class.java)
      binding.lifecycleOwner = viewLifecycleOwner

      recipesViewModel.isUpdating.observe(viewLifecycleOwner, { isUpdating ->
         isUpdating?.let {
            if (isUpdating) {
               // preparation for loading
               binding.swipeContainer.isRefreshing = true
               refreshItem?.let { it.isEnabled = false }
            } else {
               binding.swipeContainer.isRefreshing = false
               refreshItem?.let { it.isEnabled = true }
            }
         }
      })

      recipesViewModel.isLoaded.observe(viewLifecycleOwner, {
         it?.let {
            isLoaded = it
         }
      })

      initializeRecipeList()

      return binding.root
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      @Suppress("DEPRECATION")
      super.onActivityCreated(savedInstanceState)
      (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
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

      // refreshItem
      refreshItem = menu.findItem(R.id.refreshAction)
   }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return when (item.itemId) {
         R.id.refreshAction -> {
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
      binding.lifecycleOwner = viewLifecycleOwner

      // divider for recyclerview
      val dividerDecoration = DividerItemDecoration(binding.recipeList.context, LinearLayoutManager.VERTICAL)
      binding.recipeList.addItemDecoration(dividerDecoration)

      // data adapter
      adapter = RecipeListAdapter(RecipeListListener { recipeName -> recipesViewModel.onRecipeClicked(recipeName) },
                                  DbRecipeRepository.getInstance(requireActivity().application))
      binding.recipeList.adapter = adapter

      // settings
      adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

      recipesViewModel.navigateToRecipe.observe(viewLifecycleOwner, { recipe ->
         recipe?.let {
            this.findNavController()
               .navigate(RecipeListFragmentDirections.actionRecipeListFragmentToRecipeDetailFragment(recipe))
            recipesViewModel.onRecipeNavigated()
         }
      })

      settingViewModel.sorting.observe(viewLifecycleOwner, { sort ->
         currentSort = SortValue.getByValue(sort)
         recipesViewModel.sortList(currentSort!!)
         loadData()
      })

      settingViewModel.category.observe(viewLifecycleOwner, { catFilter ->
         catFilter?.let {
            // filter recipes to set category
            recipesViewModel.filterRecipesByCategory(it)
            setCategoryTitle(it)
            loadData()

            settingViewModel.categoryChanged.observe(viewLifecycleOwner, { changed ->
               changed?.let { c ->
                  if (c) {
                     binding.recipeList.postDelayed(250) {
                        binding.recipeList.smoothScrollToPosition(0)
                     }
                     settingViewModel.resetCategoryChanged()
                  }
               }
            })
         }
      })

      settingViewModel.storageAccessed.observe(viewLifecycleOwner, { sa ->
         sa?.let { storageAccessed ->
            if (storageAccessed) {
               settingViewModel.recipeDirectory.observe(viewLifecycleOwner, {
                  if (!isLoaded) {
                     it?.let { recipesViewModel.initRecipes(it) }
                  }
               })
            }
         }
      })

      recipesViewModel.categories.observe(viewLifecycleOwner, { categories ->
         categories?.let {
            var order = 1
            val activity = requireActivity() as MainActivity
            val menu = activity.getMenu()

            menu.removeGroup(R.id.menu_categories_group)
            categories.forEach { category ->
               menu.add(R.id.menu_categories_group, category.hashCode(), order++, category)
            }
         }
      })

      loadData()
   }

   /**
    * Loads the current data.
    */
   private fun loadData() {
      recipesViewModel.getRecipes().observe(viewLifecycleOwner, { recipes ->
         recipes?.let {
            adapter.submitList(it)
         }
         if (recipes.isNullOrEmpty()) {
            if (R.id.emptyConstraint == binding.switcher.nextView.id)
               binding.switcher.showNext()
         } else if (R.id.titleConstraint == binding.switcher.nextView.id) {
            binding.switcher.showNext()
         }
      })
   }

   private fun setCategoryTitle(categoryFilter: CategoryFilter) {
      // set title in Actionbar
      (activity as AppCompatActivity).supportActionBar?.title = when (categoryFilter.type) {
         CategoryFilter.CategoryFilterOption.ALL_CATEGORIES -> getString(R.string.app_name)
         CategoryFilter.CategoryFilterOption.UNCATEGORIZED -> getString(R.string.text_uncategorized)
         else -> categoryFilter.name
      }
   }

   private fun showSortOptions() {
      val sortNames = resources.getStringArray(R.array.sort_names)
      val builder = AlertDialog.Builder(requireContext())
      builder.setTitle(R.string.menu_sort_title)
      val sortValue = currentSort ?: SortValue.NAME_A_Z
      builder.setSingleChoiceItems(sortNames, sortValue.sort) { _: DialogInterface, which: Int ->
         settingViewModel.setSorting(which)
         sortDialog?.dismiss()
         sortDialog = null
         binding.recipeList.postDelayed(200) {
            binding.recipeList.smoothScrollToPosition(0)
         }
      }
      builder.setOnDismissListener { sortDialog = null }
      sortDialog = builder.show()
   }

   override fun onRefresh() {
      // load recipes from files
      recipesViewModel.initRecipes()
   }

   override fun onPause() {
      sortDialog?.dismiss()
      super.onPause()
   }
}
