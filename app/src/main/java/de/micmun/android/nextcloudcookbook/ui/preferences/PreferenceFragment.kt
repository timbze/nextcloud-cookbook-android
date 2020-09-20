/*
 * PreferenceFragment.kt.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.preferences

import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.obsez.android.lib.filechooser.ChooserDialog
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.ui.MainActivity

/**
 * Fragment for settings.
 *
 * @author MicMun
 * @version 1.4, 20.09.20
 */
class PreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
                           Preference.OnPreferenceClickListener {
   private lateinit var viewModel: PreferenceViewModel

   private lateinit var dirPreference: Preference
   private lateinit var themePreference: IntListPreference
   private lateinit var hiddenFolderPreference: CheckBoxPreference

   private var currentHiddenValue: Boolean = false

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.menu_settings_title)
   }

   override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
      setPreferencesFromResource(R.xml.preferences, rootKey)
      viewModel = ViewModelProvider(this).get(PreferenceViewModel::class.java)

      // find prefs
      dirPreference = findPreference(getString(R.string.prefkey_recipeDir))!!
      hiddenFolderPreference = findPreference(getString(R.string.prefkey_allow_hidden))!!
      themePreference = findPreference(getString(R.string.prefkey_theme))!!
      val aboutPreference: Preference = findPreference(getString(R.string.prefkey_about))!!

      // change listener
      dirPreference.onPreferenceChangeListener = this
      hiddenFolderPreference.onPreferenceChangeListener = this
      themePreference.onPreferenceChangeListener = this

      // click listener
      dirPreference.onPreferenceClickListener = this

      // observe values
      viewModel.recipeDirectory.observe(this, {
         dirPreference.summary = it.toString()
      })
      viewModel.hiddenFolder.observe(this, {
         currentHiddenValue = it

         hiddenFolderPreference.summary =
            getString(if (currentHiddenValue) R.string.pref_hidden_folder_true else R.string.pref_hidden_folder_false)
      })
      viewModel.theme.observe(this, {
         themePreference.value = it.toString()
         themePreference.summary = themePreference.entry
      })

      // about version
      val version = requireContext().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName ?: ""
      aboutPreference.title = getString(R.string.about_version, version)
   }

   override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
      when (preference) {
         dirPreference -> viewModel.setRecipeDirectory(newValue.toString())
         hiddenFolderPreference -> viewModel.setHiddenFolder(newValue as Boolean)
         themePreference -> {
            viewModel.setTheme(newValue.toString().toInt())
            // recreate activity
            TaskStackBuilder.create(context)
               .addNextIntent(Intent(activity, MainActivity::class.java))
               .addNextIntent(requireActivity().intent)
               .startActivities()
         }
         else -> return false
      }
      return true
   }

   override fun onPreferenceClick(preference: Preference?): Boolean {
      when (preference) {
         dirPreference -> {
            chooseFolder()
         }
         else -> return false
      }
      return true
   }

   /**
    * Choose dialog for picking a folder.
    */
   @Suppress("DEPRECATION")
   private fun chooseFolder() {
      ChooserDialog(requireActivity())
         .withFilter(true, currentHiddenValue)
         .withStartFile(Environment.getExternalStorageDirectory().absolutePath)
         .withChosenListener { path, _ ->
            Toast.makeText(requireActivity(), "FOLDER: $path", Toast.LENGTH_SHORT).show()
            viewModel.setRecipeDirectory(path)
         }
         .build()
         .show()
   }
}
