package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.content.res.TypedArray
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.databinding.FragmentDetailBinding

/**
 * Fragment for detail of a recipe.
 *
 * @author MicMun
 * @version 1.2, 29.06.20
 */
class RecipeDetailFragment : Fragment() {
   private lateinit var binding: FragmentDetailBinding
   private lateinit var viewModel: RecipeViewModel

   private lateinit var infoIcons: TypedArray
   private lateinit var ingredientsIcons: TypedArray
   private lateinit var instructionsIcons: TypedArray

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

      val args = RecipeDetailFragmentArgs.fromBundle(requireArguments())
      val viewModelFactory = RecipeViewModelFactory(args.recipeId, requireActivity().application)
      viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeViewModel::class.java)
      binding.lifecycleOwner = this

      infoIcons = requireActivity().obtainStyledAttributes(intArrayOf(R.attr.tab_info_icon))
      ingredientsIcons = requireActivity().obtainStyledAttributes(intArrayOf(R.attr.tab_ingredients_icon))
      instructionsIcons = requireActivity().obtainStyledAttributes(intArrayOf(R.attr.tab_instructions_icon))

      viewModel.recipe.observe(viewLifecycleOwner, Observer { recipe ->
         recipe?.let {
            initPager(recipe)
         }
      })

      return binding.root
   }

   /**
    * Initialise the view pager and tablayout with the current recipe.
    *
    * @param recipe current recipe data.
    */
   private fun initPager(recipe: Recipe) {
      binding.pager.adapter = ViewPagerAdapter(recipe)
      binding.pager.offscreenPageLimit = 3
      val tabLayout = binding.tabLayout
      TabLayoutMediator(tabLayout, binding.pager) { tab, position ->
         when (position) {
            0 -> {
               tab.text = resources.getString(R.string.tab_info_title)
               tab.icon = resources.getDrawable(infoIcons.getResourceId(0, 1), requireActivity().theme)
            }
            1 -> {
               tab.text = resources.getString(R.string.tab_ingredients_title)
               tab.icon = resources.getDrawable(ingredientsIcons.getResourceId(0, 1), requireActivity().theme)
            }
            2 -> {
               tab.text = resources.getString(R.string.tab_instructions_title)
               tab.icon = resources.getDrawable(instructionsIcons.getResourceId(0, 1), requireActivity().theme)
            }
         }
      }.attach()
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      val title = viewModel.recipe.value!!.name
      (activity as AppCompatActivity).supportActionBar?.title = title
   }
}
