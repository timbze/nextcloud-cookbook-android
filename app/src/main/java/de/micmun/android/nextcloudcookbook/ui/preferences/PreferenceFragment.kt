/*
 * PreferenceFragment.kt.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.preferences

import android.Manifest.permission
import android.app.TaskStackBuilder
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.anggrayudi.storage.callback.FolderPickerCallback
import com.anggrayudi.storage.callback.StorageAccessCallback
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
 * @version 1.5, 07.04.21
 */
class PreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
                           Preference.OnPreferenceClickListener {
   private lateinit var viewModel: PreferenceViewModel

   private lateinit var dirPreference: Preference
   private lateinit var themePreference: IntListPreference

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
      themePreference = findPreference(getString(R.string.prefkey_theme))!!
      val aboutPreference: Preference = findPreference(getString(R.string.prefkey_about))!!

      // change listener
      dirPreference.onPreferenceChangeListener = this
      themePreference.onPreferenceChangeListener = this

      // click listener
      dirPreference.onPreferenceClickListener = this

      // observe values
      viewModel.recipeDirectory.observe(this, {
         val summary =
            if (it.isEmpty()) "" else StorageManager.getDocumentFromString(requireContext(), it)?.absolutePath ?: ""
         dirPreference.summary = summary
      })

      viewModel.theme.observe(this, {
         themePreference.value = it.toString()
         themePreference.summary = themePreference.entry
      })

      setupSimpleStorage()
      viewModel.storageAccesssed.observe(this, { isAccess ->
         if (!isAccess) {
            Dexter.withContext(requireContext())
               .withPermissions(permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE)
               .withListener(object : BaseMultiplePermissionsListener() {
                  override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                     val grantStatus = if (report.areAllPermissionsGranted()) getString(R.string.permission_granted)
                     else getString(R.string.permission_denied)

                     if (grantStatus == getString(R.string.permission_granted)) {
                        viewModel.setStorageAccessed(true)
                     } else {
                        viewModel.setStorageAccessed(false)
                     }
                  }
               }).check()
         }
      })

      // about version
      val version = requireContext().packageManager.getPackageInfo(requireActivity().packageName, 0).versionName ?: ""
      aboutPreference.title = getString(R.string.about_version, version)
   }

   override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
      when (preference) {
         dirPreference -> viewModel.setRecipeDirectory(newValue.toString())
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
                     getString(R.string.permission_please_grant),
                     Toast.LENGTH_SHORT
                  ).show()
               }
            }
         }).check()
   }

   private fun setupSimpleStorage() {
      storageManager.storage?.storageAccessCallback = object : StorageAccessCallback {
         override fun onRootPathNotSelected(requestCode: Int, rootPath: String, rootStorageType: StorageType,
                                            uri: Uri) = MaterialDialog(requireActivity())
            .message(text = getString(R.string.permission_select_root, rootPath))
            .negativeButton(android.R.string.cancel)
            .positiveButton {
               storageManager.storage?.requestStorageAccess(MainActivity.REQUEST_CODE_STORAGE_ACCESS, rootStorageType)
            }.show()

         override fun onCancelledByUser(requestCode: Int) {
            Toast.makeText(requireContext(), getString(R.string.permission_canceled), Toast.LENGTH_SHORT).show()
         }

         override fun onStoragePermissionDenied(requestCode: Int) {
            requestStoragePermission()
         }

         override fun onRootPathPermissionGranted(requestCode: Int, root: DocumentFile) {
            Toast.makeText(requireContext(), getString(R.string.permission_ok_message, root.absolutePath),
                           Toast.LENGTH_SHORT).show()
         }
      }
   }

   private fun setupFolderPickerCallback() {
      storageManager.storage?.folderPickerCallback = object : FolderPickerCallback {
         override fun onStoragePermissionDenied(requestCode: Int) {
            requestStoragePermission()
         }

         override fun onStorageAccessDenied(requestCode: Int, folder: DocumentFile?, storageType: StorageType?) {
            if (storageType == null) {
               requestStoragePermission()
               return
            }

            MaterialDialog(requireActivity())
               .message(text = getString(R.string.permission_warning_nowrite))
               .negativeButton(android.R.string.cancel)
               .positiveButton {
                  storageManager.storage?.requestStorageAccess(REQUEST_CODE_STORAGE_ACCESS, storageType)
               }.show()
         }

         override fun onFolderSelected(requestCode: Int, folder: DocumentFile) {
            Toast.makeText(requireActivity(), getString(R.string.folder_pick_success, folder.absolutePath),
                           Toast.LENGTH_SHORT).show()
            viewModel.setRecipeDirectory(folder.uri.toString())
         }

         override fun onCancelledByUser(requestCode: Int) {
            Toast.makeText(context, getString(R.string.folder_pick_cancel), Toast.LENGTH_SHORT).show()
         }
      }
   }
}
