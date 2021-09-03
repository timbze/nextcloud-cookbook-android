/*
 * MainActivity.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui

import android.app.SearchManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import de.micmun.android.nextcloudcookbook.MainApplication
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.CategoryFilter
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.databinding.ActivityMainBinding
import de.micmun.android.nextcloudcookbook.settings.PreferenceDao
import de.micmun.android.nextcloudcookbook.ui.recipelist.RecipeListFragmentDirections

/**
 * Main Activity of the app.
 *
 * @author MicMun
 * @version 1.6, 29.08.21
 */
class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding
   private lateinit var drawerLayout: DrawerLayout
   private lateinit var currentSettingViewModel: CurrentSettingViewModel

   override fun onCreate(savedInstanceState: Bundle?) {
      // apply theme
      when (PreferenceDao.getInstance(application).getThemeSync()) {
         0 -> setTheme(R.style.AppTheme_Light)
         1 -> setTheme(R.style.AppTheme_Dark)
         2 -> setTheme(systemTheme())
      }

      super.onCreate(savedInstanceState)
      binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

      // toolbar
      setSupportActionBar(binding.toolbar.myToolbar)

      // drawer layout
      drawerLayout = binding.drawerLayout

      // navigation
      val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
      val navController = navHostFragment.findNavController()
      NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
      NavigationUI.setupWithNavController(binding.navView, navController)

      // prevent nav gesture if not on start destination
      navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, _: Bundle? ->
         if (nd.id == nc.graph.startDestination) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
         } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
         }
      }

      // settings
      val factory = CurrentSettingViewModelFactory(MainApplication.AppContext)
      currentSettingViewModel =
         ViewModelProvider(MainApplication.AppContext, factory).get(CurrentSettingViewModel::class.java)
      binding.navView.setNavigationItemSelectedListener { item ->
         val currentCat = when (item.itemId) {
            R.id.menu_all_categories -> CategoryFilter(CategoryFilter.CategoryFilterOption.ALL_CATEGORIES)
            R.id.menu_uncategorized -> CategoryFilter(CategoryFilter.CategoryFilterOption.UNCATEGORIZED)
            else -> CategoryFilter(CategoryFilter.CategoryFilterOption.CATEGORY, item.title.toString())
         }
         currentSettingViewModel.setNewCategory(currentCat)
         drawerLayout.closeDrawers()
         true
      }

      // permission for storage
      currentSettingViewModel.storageAccessed.observe(this, {
         it?.let { access ->
            if (!access) {
               storagePermissions()
            }
         }
      })

      handleIntent(intent)
   }

   override fun onNewIntent(intent: Intent?) {
      super.onNewIntent(intent)
      handleIntent(intent)
   }

   fun getMenu(): Menu {
      return binding.navView.menu
   }

   override fun onSupportNavigateUp(): Boolean {
      val navControler = this.findNavController(R.id.navHostFragment)
      return NavigationUI.navigateUp(navControler, drawerLayout)
   }

   private fun systemTheme(): Int {
      return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
         Configuration.UI_MODE_NIGHT_YES -> R.style.AppTheme_Dark
         else -> R.style.AppTheme_Light
      }
   }

   /**
    * Handle intent for search.
    *
    * @param intent incoming intent.
    */
   private fun handleIntent(intent: Intent?) {
      if (Intent.ACTION_SEARCH == intent?.action) {
         val query = intent.getStringExtra(SearchManager.QUERY)
         if (query != null) {
            val filter = RecipeFilter(RecipeFilter.QueryType.QUERY_NAME, query)
            val navControler = this.findNavController(R.id.navHostFragment)
            navControler.navigate(
               RecipeListFragmentDirections.actionRecipeListFragmentToRecipeSearchFragment(filter))
         }
      }
   }

   /**
    * Handles the default storage permissions.
    */
   private fun storagePermissions() {
      val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
         listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
      } else {
         listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
      }
      val preference = PreferenceDao.getInstance(MainApplication.AppContext)

      Dexter.withContext(this)
         .withPermissions(permissions)
         .withListener(object : BaseMultiplePermissionsListener() {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
               if (report?.areAllPermissionsGranted() == true) {
                  preference.setStorageAccess(true)
               } else {
                  preference.setStorageAccess(false)
                  Snackbar.make(binding.root, "No storage access!", Snackbar.LENGTH_LONG).show()
               }
            }
         })
         .check()
   }
}
