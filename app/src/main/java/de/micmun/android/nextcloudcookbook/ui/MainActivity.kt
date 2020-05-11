/*
 * MainActivity.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
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
 * @version 1.0, 10.05.20
 */
class MainActivity : AppCompatActivity() {
   private lateinit var binding: ActivityMainBinding

   override fun onCreate(savedInstanceState: Bundle?) {
      // apply theme
      when (PreferenceDao.getInstance(application).getThemeSync()) {
         0 -> setTheme(R.style.AppTheme_Light)
         1 -> setTheme(R.style.AppTheme_Dark)
         2 -> setTheme(systemTheme())
      }

      super.onCreate(savedInstanceState)
      binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

      // navigation
      val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
      val navController = navHostFragment.navController
      NavigationUI.setupActionBarWithNavController(this, navController)

      // permissions
      Dexter.withContext(this)
         .withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
         )
         .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
               if (!report!!.areAllPermissionsGranted()) {
                  Snackbar.make(
                     binding.root,
                     "Access denied! App need storage access, please activate in the settings.",
                     Snackbar.LENGTH_LONG
                  ).show()
               }
            }

            override fun onPermissionRationaleShouldBeShown(
               p0: MutableList<PermissionRequest>?,
               p1: PermissionToken?
            ) {
            }
         })
   }

   override fun onSupportNavigateUp(): Boolean {
      val navControler = this.findNavController(R.id.navHostFragment)
      return navControler.navigateUp()
   }

   private fun systemTheme(): Int {
      return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
         Configuration.UI_MODE_NIGHT_YES -> R.style.AppTheme_Dark
         else -> R.style.AppTheme_Light
      }
   }
}
