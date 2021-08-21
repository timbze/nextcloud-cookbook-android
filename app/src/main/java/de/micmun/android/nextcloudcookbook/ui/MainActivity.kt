/*
 * MainActivity.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui

import android.Manifest
import android.app.SearchManager
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.afollestad.materialdialogs.MaterialDialog
import com.anggrayudi.storage.SimpleStorage
import com.anggrayudi.storage.callback.StorageAccessCallback
import com.anggrayudi.storage.file.StorageType
import com.anggrayudi.storage.file.absolutePath
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import de.micmun.android.nextcloudcookbook.MainApplication
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.RecipeFilter
import de.micmun.android.nextcloudcookbook.databinding.ActivityMainBinding
import de.micmun.android.nextcloudcookbook.settings.PreferenceDao
import de.micmun.android.nextcloudcookbook.data.CategoryFilter
import de.micmun.android.nextcloudcookbook.ui.recipelist.RecipeListFragmentDirections
import de.micmun.android.nextcloudcookbook.util.StorageManager

/**
 * Main Activity of the app.
 *
 * @author MicMun
 * @version 1.5, 07.04.21
 */
class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding
   private lateinit var drawerLayout: DrawerLayout
   private lateinit var currentSettingViewModel: CurrentSettingViewModel
   private lateinit var storageManager: StorageManager

   companion object {
      const val REQUEST_CODE_STORAGE_ACCESS = 1
   }

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
      currentSettingViewModel = ViewModelProvider(MainApplication.AppContext, factory).get(CurrentSettingViewModel::class.java)
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

      setupSimpleStorage(savedInstanceState)
      val preferences = PreferenceDao.getInstance(application)
      preferences.isStorageAccessed().observe(this, { isAccess ->
         if (!isAccess) {
            Dexter.withContext(this)
               .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
               .withListener(object : BaseMultiplePermissionsListener() {
                  override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                     val grantStatus = if (report.areAllPermissionsGranted()) "granted" else "denied"

                     if (grantStatus == "granted") {
                        preferences.setStorageAccess(true)
                     } else {
                        preferences.setStorageAccess(false)
                     }
                  }
               }).check()
         }
      })

      handleIntent(intent)
   }

   private fun setupSimpleStorage(savedInstanceState: Bundle?) {
      storageManager = StorageManager.getInstance()
      storageManager.storage = SimpleStorage(this, savedInstanceState)

      storageManager.storage?.storageAccessCallback = object : StorageAccessCallback {
         override fun onRootPathNotSelected(
            requestCode: Int,
            rootPath: String,
            rootStorageType: StorageType,
            uri: Uri
         ) {
            MaterialDialog(this@MainActivity)
               .message(text = "Please select $rootPath")
               .negativeButton(android.R.string.cancel)
               .positiveButton {
                  storageManager.storage?.requestStorageAccess(REQUEST_CODE_STORAGE_ACCESS, rootStorageType)
               }.show()
         }

         override fun onCancelledByUser(requestCode: Int) {
            Toast.makeText(baseContext, "Cancelled by user", Toast.LENGTH_SHORT).show()
         }

         override fun onStoragePermissionDenied(requestCode: Int) {
            requestStoragePermission()
         }

         override fun onRootPathPermissionGranted(requestCode: Int, root: DocumentFile) {
            Toast.makeText(
               baseContext,
               "Storage access has been granted for ${root.absolutePath}",
               Toast.LENGTH_SHORT
            ).show()
         }
      }
   }

   private fun requestStoragePermission() {
      Dexter.withContext(this)
         .withPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
         )
         .withListener(object : BaseMultiplePermissionsListener() {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
               if (report.areAllPermissionsGranted()) {
                  storageManager.storage?.requestStorageAccess(REQUEST_CODE_STORAGE_ACCESS)
               } else {
                  Toast.makeText(
                     baseContext,
                     "Please grant storage permissions",
                     Toast.LENGTH_SHORT
                  ).show()
               }
            }
         }).check()
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

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      storageManager.storage?.onActivityResult(requestCode, resultCode, data)
   }

   override fun onSaveInstanceState(outState: Bundle) {
      storageManager.storage?.onSaveInstanceState(outState)
      super.onSaveInstanceState(outState)
   }

   override fun onRestoreInstanceState(savedInstanceState: Bundle) {
      super.onRestoreInstanceState(savedInstanceState)
      storageManager.storage?.onRestoreInstanceState(savedInstanceState)
   }
}
