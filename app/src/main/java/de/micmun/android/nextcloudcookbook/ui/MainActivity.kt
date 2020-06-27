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
import android.provider.Settings
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.snackbar.Snackbar
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.PreferenceDao
import de.micmun.android.nextcloudcookbook.databinding.ActivityMainBinding
import de.micmun.android.nextcloudcookbook.ui.recipelist.RecipeListFragmentDirections

/**
 * Main Activity of the app.
 *
 * @author MicMun
 * @version 1.3, 27.06.20
 */
class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding
   private lateinit var drawerLayout: DrawerLayout
   var categorySelectedListener: CategorySelectedListener? = null
   private var currentCatId: Int = -1

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
      binding.navView.setNavigationItemSelectedListener { item ->
         currentCatId = when (item.itemId) {
            R.id.menu_all_categories -> -1
            else -> item.itemId
         }
         categorySelectedListener?.onCategorySelected(item.itemId)
         drawerLayout.closeDrawers()
         true
      }

      // permissions
      permissionsBuilder(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
         .build()
         .send { result ->
            // Handle the result
            if (!result.allGranted()) {
               val message = Snackbar.make(binding.root,
                                           resources.getString(R.string.permissions_error_message),
                                           Snackbar.LENGTH_LONG
               )
               message.setAction(R.string.menu_settings_title) {
                  // create link to app settings
                  val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                  val uri = Uri.fromParts("package", this.packageName, null)
                  intent.data = uri
                  startActivity(intent)
               }
               message.show()
            }
         }

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

   private fun handleIntent(intent: Intent?) {
      if (Intent.ACTION_SEARCH == intent?.action) {
         val query = intent.getStringExtra(SearchManager.QUERY)
         if (query != null) {
            val navControler = this.findNavController(R.id.navHostFragment)
            navControler.navigate(
               RecipeListFragmentDirections.actionRecipeListFragmentToRecipeSearchFragment(query, currentCatId))
         }
      }
   }
}

/**
 * Listener for category selection.
 *
 * @author MicMun
 * @version 1.0, 21.06.20
 */
interface CategorySelectedListener {
   fun onCategorySelected(id: Int)
}
