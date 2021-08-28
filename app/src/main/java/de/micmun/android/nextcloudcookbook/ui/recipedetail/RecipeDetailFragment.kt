package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.content.res.TypedArray
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import de.micmun.android.nextcloudcookbook.MainApplication
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.databinding.FragmentDetailBinding
import de.micmun.android.nextcloudcookbook.db.model.DbRecipe
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModel
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModelFactory

/**
 * Fragment for detail of a recipe.
 *
 * @author MicMun
 * @version 2.1, 28.08.21
 */
class RecipeDetailFragment : Fragment(), CookTimeClickListener {
   private lateinit var binding: FragmentDetailBinding
   private lateinit var viewModel: RecipeViewModel
   private lateinit var settingViewModel: CurrentSettingViewModel

   private lateinit var infoIcons: TypedArray
   private lateinit var ingredientsIcons: TypedArray
   private lateinit var instructionsIcons: TypedArray
   private lateinit var nutritionsIcons: TypedArray
   private var adapter: ViewPagerAdapter? = null

   private var currentPage = 0

   private var recipeId = -1L
   private var isServiceStarted = false

   companion object {
      private const val KEY_CURRENT_PAGE = "current_page"
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      val args = RecipeDetailFragmentArgs.fromBundle(requireArguments())

      if (savedInstanceState != null) {
         currentPage = savedInstanceState[KEY_CURRENT_PAGE] as Int
         recipeId = args.recipeId
      } else {
         recipeId = args.recipeId
      }
      isServiceStarted = args.isServiceStarted
   }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

      // if the service calls this fragment, navigate to CooktimerFragment
      if (isServiceStarted) {
         isServiceStarted = false
         findNavController().navigate(
            RecipeDetailFragmentDirections.actionRecipeDetailFragmentToCooktimerFragment(recipeId))
      }

      // Settings view model
      val factory = CurrentSettingViewModelFactory(MainApplication.AppContext)
      settingViewModel =
         ViewModelProvider(MainApplication.AppContext, factory).get(CurrentSettingViewModel::class.java)

      val viewModelFactory = RecipeViewModelFactory(recipeId, requireActivity().application)
      viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeViewModel::class.java)
      binding.lifecycleOwner = viewLifecycleOwner

      val orientation = resources.configuration.orientation

      infoIcons = requireActivity().obtainStyledAttributes(intArrayOf(R.attr.tab_info_icon))
      ingredientsIcons = requireActivity().obtainStyledAttributes(intArrayOf(R.attr.tab_ingredients_icon))
      instructionsIcons = requireActivity().obtainStyledAttributes(intArrayOf(R.attr.tab_instructions_icon))
      nutritionsIcons = requireActivity().obtainStyledAttributes(intArrayOf(R.attr.tab_nutritions_icon))

      viewModel.recipe.observe(viewLifecycleOwner, { recipe ->
         recipe?.let {
            initPager(it, orientation)
            setTitle(it.recipeCore.name)
         }
      })

      return binding.root
   }

   /**
    * Initialise the view pager and tablayout with the current recipe.
    *
    * @param recipe current recipe data.
    */
   private fun initPager(recipe: DbRecipe, orientation: Int) {
      adapter = ViewPagerAdapter(recipe, orientation, this)
      binding.pager.adapter = adapter
      binding.pager.offscreenPageLimit = adapter!!.itemCount
      val tabLayout = binding.tabLayout
      TabLayoutMediator(tabLayout, binding.pager) { tab, position ->
         when (adapter!!.getItemViewType(position)) {
            ViewPagerAdapter.TYPE_INFO -> {
               tab.text = resources.getString(R.string.tab_info_title)
               tab.icon = ResourcesCompat.getDrawable(resources, infoIcons.getResourceId(0, 1), requireActivity().theme)
            }
            ViewPagerAdapter.TYPE_INGREDIENTS -> {
               tab.text = resources.getString(R.string.tab_ingredients_title)
               tab.icon =
                  ResourcesCompat.getDrawable(resources, ingredientsIcons.getResourceId(0, 1), requireActivity().theme)
            }
            ViewPagerAdapter.TYPE_INSTRUCTIONS -> {
               tab.text = resources.getString(R.string.tab_instructions_title)
               tab.icon =
                  ResourcesCompat.getDrawable(resources, instructionsIcons.getResourceId(0, 1), requireActivity().theme)
            }
            ViewPagerAdapter.TYPE_NUTRITIONS -> {
               tab.text = resources.getString(R.string.tab_nutritions_title)
               tab.icon =
                  ResourcesCompat.getDrawable(resources, nutritionsIcons.getResourceId(0, 1), requireActivity().theme)
            }
            ViewPagerAdapter.TYPE_INGRED_AND_INSTRUCT -> {
               tab.text = "${resources.getString(R.string.tab_ingredients_title)} & ${
                  resources.getString(R.string.tab_instructions_title)
               }"
               tab.icon =
                  ResourcesCompat.getDrawable(resources, instructionsIcons.getResourceId(0, 1), requireActivity().theme)
            }
         }
      }.attach()

      binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
         override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            currentPage = position

            val type = adapter!!.getItemViewType(position)

            if (type == ViewPagerAdapter.TYPE_INSTRUCTIONS || type == ViewPagerAdapter.TYPE_INGRED_AND_INSTRUCT) {
               activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
               activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
         }
      })
   }

   override fun onSaveInstanceState(outState: Bundle) {
      outState.putInt(KEY_CURRENT_PAGE, currentPage)
      super.onSaveInstanceState(outState)
   }

   override fun onPause() {
      activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
      super.onPause()
   }

   override fun onResume() {
      super.onResume()
      if (adapter != null) {
         val type = adapter!!.getItemViewType(currentPage)
         if (type == ViewPagerAdapter.TYPE_INSTRUCTIONS || type == ViewPagerAdapter.TYPE_INGRED_AND_INSTRUCT) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
         }
      }
   }

   @Suppress("DEPRECATION")
   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      val title = viewModel.recipe.value?.recipeCore?.name
      title?.let {
         setTitle(it)
      }
   }

   private fun setTitle(title: String) {
      (activity as AppCompatActivity).supportActionBar?.title = title
   }

   override fun onClick(recipe: DbRecipe) {
      findNavController()
         .navigate(
            RecipeDetailFragmentDirections.actionRecipeDetailFragmentToCooktimerFragment(recipe.recipeCore.id))
   }
}
