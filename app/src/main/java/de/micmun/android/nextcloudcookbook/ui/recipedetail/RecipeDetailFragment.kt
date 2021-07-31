package de.micmun.android.nextcloudcookbook.ui.recipedetail

import android.Manifest
import android.content.Intent
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.databinding.FragmentDetailBinding
import de.micmun.android.nextcloudcookbook.db.model.DbRecipe
import de.micmun.android.nextcloudcookbook.services.CooktimerService
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModel
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModelFactory
import de.micmun.android.nextcloudcookbook.ui.MainActivity

/**
 * Fragment for detail of a recipe.
 *
 * @author MicMun
 * @version 1.9, 31.07.21
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
   private var remains = -1L
   private var serviceStarted = false

   companion object {
      private const val KEY_CURRENT_PAGE = "current_page"
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      val args = RecipeDetailFragmentArgs.fromBundle(requireArguments())
      recipeId = args.recipeId
      remains = args.remains
      serviceStarted = remains != -1L
   }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

      // Settings view model
      val factory = CurrentSettingViewModelFactory(MainActivity.mainApplication)
      settingViewModel =
         ViewModelProvider(MainActivity.mainApplication, factory).get(CurrentSettingViewModel::class.java)

      // if the service calls this fragment, navigate to CooktimerFragment with remaining time
      if (serviceStarted) {
         serviceStarted = false
         stopService()
         findNavController().navigate(
            RecipeDetailFragmentDirections.actionRecipeDetailFragmentToCooktimerFragment(recipeId, remains))
      }

      val viewModelFactory = RecipeViewModelFactory(recipeId, requireActivity().application)
      viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeViewModel::class.java)
      binding.lifecycleOwner = viewLifecycleOwner

      settingViewModel.remains.observe(viewLifecycleOwner, {
         it?.let { remains ->
            startService(remains)
            settingViewModel.setRemains(null)
         }
      })

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

      if (savedInstanceState != null) {
         currentPage = savedInstanceState[KEY_CURRENT_PAGE] as Int
      }

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
         .navigate(RecipeDetailFragmentDirections.actionRecipeDetailFragmentToCooktimerFragment(recipe.recipeCore.id))
   }

   private fun startService(remains: Long) {
      if (checkServicePermission()) {
         val intent = cooktimeService()
         intent.putExtra("RECIPE_ID", recipeId)
         intent.putExtra("COOK_TIME", remains)
         requireActivity().startService(intent)
      }
   }

   private fun stopService() {
      requireActivity().stopService(cooktimeService())
   }

   /**
    * Returns <code>true</code>, if the app has the permission to start an foreground service.
    *
    * @return <code>true</code>, if the app has the permission to start an foreground service.
    */
   private fun checkServicePermission(): Boolean {
      var result = true

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
         Dexter.withContext(requireContext())
            .withPermissions(Manifest.permission.FOREGROUND_SERVICE)
            .withListener(object : BaseMultiplePermissionsListener() {
               override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                  result = report.areAllPermissionsGranted()
               }
            }).check()
      } else
         result = true

      return result
   }

   /**
    * Returns the intent for the countdown service.
    *
    * @return the intent for the countdown service.
    */
   private fun cooktimeService() = Intent(activity, CooktimerService::class.java)
}
