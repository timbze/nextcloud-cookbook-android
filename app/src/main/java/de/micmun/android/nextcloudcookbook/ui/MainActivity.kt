/*
 * MainActivity.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
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
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.data.PreferenceDao
import de.micmun.android.nextcloudcookbook.databinding.ActivityMainBinding

/**
 * Main Activity of the app.
 *
 * @author MicMun
 * @version 1.1, 21.06.20
 */
class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding
   private lateinit var drawerLayout: DrawerLayout
   var categorySelectedListener: CategorySelectedListener? = null

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
      navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
         if (nd.id == nc.graph.startDestination) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
         } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
         }
      }
      binding.navView.setNavigationItemSelectedListener { item ->
         categorySelectedListener?.onCategorySelected(item.itemId)
         drawerLayout.closeDrawers()
         true
      }

      // permissions
      Dexter.withContext(this)
         .withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
         )
         .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
               if (!report!!.areAllPermissionsGranted()) {
                  Snackbar.make(binding.root,
                                "Access denied! App need storage access, please activate in the settings.",
                                Snackbar.LENGTH_LONG
                  ).show()
               }
            }

            override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?,
                                                            p1: PermissionToken?
            ) {
            }
         })
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
