/*
 * PreferenceFragment.kt.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.preferences

import android.Manifest.permission
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.anggrayudi.storage.callback.FolderPickerCallback
import com.anggrayudi.storage.file.StorageType
import com.anggrayudi.storage.file.absolutePath
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.ui.MainActivity
import de.micmun.android.nextcloudcookbook.util.StorageManager

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
   private val storageManager = StorageManager.getInstance()

   companion object {
      const val REQUEST_CODE_STORAGE_ACCESS = 1
      const val REQUEST_CODE_PICK_FOLDER = 2
   }

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
         dirPreference.summary = StorageManager.getDocumentFromString(requireContext(), it)?.absolutePath ?: ""
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
   private fun chooseFolder() {
      setupFolderPickerCallback()
      storageManager.storage?.openFolderPicker(REQUEST_CODE_PICK_FOLDER)
   }

   private fun requestStoragePermission() {
      Dexter.withContext(context)
         .withPermissions(
            permission.WRITE_EXTERNAL_STORAGE,
            permission.READ_EXTERNAL_STORAGE
         )
         .withListener(object : BaseMultiplePermissionsListener() {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
               if (report.areAllPermissionsGranted()) {
                  storageManager.storage?.requestStorageAccess(REQUEST_CODE_STORAGE_ACCESS)
               } else {
                  Toast.makeText(
                     context,
                     "Please grant storage permissions",
                     Toast.LENGTH_SHORT
                  ).show()
               }
            }
         }).check()
   }

   private fun setupFolderPickerCallback() {
      storageManager.storage?.folderPickerCallback = object : FolderPickerCallback {
         override fun onStoragePermissionDenied(requestCode: Int) {
            requestStoragePermission()
         }

         override fun onStorageAccessDenied(
            requestCode: Int,
            folder: DocumentFile?,
            storageType: StorageType?
         ) {
            if (storageType == null) {
               requestStoragePermission()
               return
            }

            MaterialDialog(requireActivity())
               .message(
                  text = "You have no write access to this storage, thus selecting this folder is useless." +
                         "\nWould you like to grant access to this folder?"
               )
               .negativeButton(android.R.string.cancel)
               .positiveButton {
                  storageManager.storage?.requestStorageAccess(REQUEST_CODE_STORAGE_ACCESS, storageType)
               }.show()
         }

         override fun onFolderSelected(requestCode: Int, folder: DocumentFile) {
            Toast.makeText(requireActivity(), "FOLDER: ${folder.absolutePath}", Toast.LENGTH_SHORT).show()
            viewModel.setRecipeDirectory(folder.uri.toString())
         }

         override fun onCancelledByUser(requestCode: Int) {
            Toast.makeText(context, "Folder picker cancelled by user", Toast.LENGTH_SHORT).show()
         }
      }
   }
}
