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
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.databinding.FragmentRecipelistBinding

/**
 * A simple [Fragment] subclass.
 */
class RecipeListFragment : Fragment() {
   private lateinit var binding: FragmentRecipelistBinding
   private lateinit var viewModel: RecipeListViewModel

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipelist, container, false)
      setHasOptionsMenu(true)

      initializeRecipeList()

      return binding.root
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
   }

   override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
      super.onCreateOptionsMenu(menu, inflater)
      inflater.inflate(R.menu.overflow_menu, menu)
   }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return NavigationUI
                .onNavDestinationSelected(item,
                                          requireView().findNavController()) || super.onOptionsItemSelected(item)
   }

   private fun initializeRecipeList() {
      viewModel = ViewModelProvider(this).get(RecipeListViewModel::class.java)
      binding.recipeListViewModel = viewModel
      binding.lifecycleOwner = this

      // divider for recyclerview
      val dividerDecoration = DividerItemDecoration(binding.recipeList.context, LinearLayoutManager.VERTICAL)
      binding.recipeList.addItemDecoration(dividerDecoration)

      // data adapter
      val adapter = RecipeAdapter(RecipeListener { recipeId -> viewModel.onRecipeClicked(recipeId) })
      binding.recipeList.adapter = adapter

      viewModel.recipeList.observe(viewLifecycleOwner, Observer {
         it?.let {
            adapter.submitList(it)

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
      viewModel.recipeDirectory.observe(viewLifecycleOwner, Observer { path -> viewModel.initRecipes(path) })
      viewModel.descSorting.observe(viewLifecycleOwner, Observer { desc -> viewModel.sortRecipes(desc) })
   }
}
