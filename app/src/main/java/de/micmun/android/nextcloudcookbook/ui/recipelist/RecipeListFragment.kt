package de.micmun.android.nextcloudcookbook.ui.recipelist

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
import de.micmun.android.nextcloudcookbook.ui.CategorySelectedListener
import de.micmun.android.nextcloudcookbook.ui.MainActivity

/**
 * Fragment for list of recipes.
 *
 * @author MicMun
 * @version 1.4, 21.06.20
 */
class RecipeListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, CategorySelectedListener {
   private lateinit var binding: FragmentRecipelistBinding
   private lateinit var viewModel: RecipeListViewModel

   private var recipePath: String? = null
   private var refreshItem: MenuItem? = null

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipelist, container, false)
      setHasOptionsMenu(true)

      binding.swipeContainer.setOnRefreshListener(this)

      return binding.root
   }

   override fun onResume() {
      super.onResume()
      (activity as MainActivity).categorySelectedListener = this
      initializeRecipeList()
   }

   override fun onPause() {
      (activity as MainActivity).categorySelectedListener = null
      super.onPause()
   }

   override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
      super.onCreateOptionsMenu(menu, inflater)
      inflater.inflate(R.menu.overflow_menu, menu)
   }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return when (item.itemId) {
         R.id.refreshAction -> {
            refreshItem = item
            onRefresh()
            refreshItem = null
            true
         }
         else -> NavigationUI
                    .onNavDestinationSelected(item,
                                              requireView().findNavController()) || super.onOptionsItemSelected(item)
      }
   }

   private fun initializeRecipeList() {
      viewModel = ViewModelProvider(this).get(RecipeListViewModel::class.java)
      binding.recipeListViewModel = viewModel
      binding.lifecycleOwner = this

      // divider for recyclerview
      val dividerDecoration = DividerItemDecoration(binding.recipeList.context, LinearLayoutManager.VERTICAL)
      binding.recipeList.addItemDecoration(dividerDecoration)

      // data adapter
      val adapter = RecipeListAdapter(RecipeListListener { recipeId -> viewModel.onRecipeClicked(recipeId) })
      binding.recipeList.adapter = adapter

      viewModel.recipeList.observe(viewLifecycleOwner, Observer {
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
      viewModel.navigateToRecipe.observe(viewLifecycleOwner, Observer { recipe ->
         recipe?.let {
            this.findNavController()
               .navigate(RecipeListFragmentDirections.actionRecipeListFragmentToRecipeDetailFragment(recipe))
            viewModel.onRecipeNavigated()
         }
      })
      viewModel.recipeDirectory.observe(viewLifecycleOwner, Observer { path ->
         recipePath = path
         onRefresh()
      })
      viewModel.filterCategory.observe(viewLifecycleOwner, Observer { id ->
         var option = RecipeListViewModel.CategoryFilterOption.CATEGORY

         when (id) {
            R.id.menu_all_categories -> {
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
                  RecipeRepository.getInstance().getCategoryTitle(id)
            }
         }
         // filter recipes to set categorie
         viewModel.filterRecipes(option)
      })
   }

   override fun onRefresh() {
      // preparation for loading
      binding.swipeContainer.isRefreshing = true
      refreshItem?.let { it.isEnabled = false }

      // load recipes and set categories in menu
      val activity = requireActivity() as MainActivity
      val menu = activity.getMenu()
      recipePath?.let { viewModel.initRecipes(it, menu, refreshItem != null) }

      // end of loading
      binding.swipeContainer.isRefreshing = false
      refreshItem?.let { it.isEnabled = true }
   }

   override fun onCategorySelected(id: Int) {
      viewModel.onCategoryClicked(id)
   }
}
