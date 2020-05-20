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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.obsez.android.lib.filechooser.ChooserDialog
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.ui.MainActivity

/**
 * Fragment for settings.
 *
 * @author MicMun
 * @version 1.0, 06.03.20
 */
class PreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
                           Preference.OnPreferenceClickListener {
   private lateinit var viewModel: PreferenceViewModel

   private lateinit var dirPreference: Preference
   private lateinit var sortPreference: SwitchPreference
   private lateinit var themePreference: IntListPreference

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.menu_settings_title)
   }

   override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
      setPreferencesFromResource(R.xml.preferences, rootKey)
      viewModel = ViewModelProvider(this).get(PreferenceViewModel::class.java)

      // find prefs
      dirPreference = findPreference(getString(R.string.prefkey_recipeDir))!!
      sortPreference = findPreference(getString(R.string.prefkey_descSort))!!
      themePreference = findPreference(getString(R.string.prefkey_theme))!!

      // change listener
      dirPreference.onPreferenceChangeListener = this
      sortPreference.onPreferenceChangeListener = this
      themePreference.onPreferenceChangeListener = this

      // click listener
      dirPreference.onPreferenceClickListener = this

      // observe values
      viewModel.recipeDirectory.observe(this, Observer {
         dirPreference.summary = it.toString()
      })
      viewModel.descSorting.observe(this, Observer { sortPreference.isChecked = it })
      viewModel.theme.observe(this, Observer {
         themePreference.value = it.toString()
         themePreference.summary = themePreference.entry
      })
   }

   override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
      when (preference) {
         dirPreference -> viewModel.setRecipeDirectory(newValue.toString())
         sortPreference -> viewModel.setDescSorting(newValue.toString().toBoolean())
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
         sortPreference -> viewModel.setDescSorting((preference as SwitchPreference).isChecked)
         else -> return false
      }
      return true
   }

   /**
    * Choose dialog for picking a folder.
    */
   private fun chooseFolder() {
      ChooserDialog(requireActivity())
         .withFilter(true, false)
         .withStartFile(Environment.getExternalStorageDirectory().absolutePath)
         .withChosenListener { path, _ ->
            Toast.makeText(requireActivity(), "FOLDER: $path", Toast.LENGTH_SHORT).show()
            viewModel.setRecipeDirectory(path)
         }
         .build()
         .show()
   }
}
