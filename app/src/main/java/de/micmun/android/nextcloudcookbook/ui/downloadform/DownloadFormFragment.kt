/*
 * SearchFormFragment.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.downloadform

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.anggrayudi.storage.file.findFolder
import com.anggrayudi.storage.file.openOutputStream
import com.beust.klaxon.KlaxonException
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.databinding.FragmentDownloadFormBinding
import de.micmun.android.nextcloudcookbook.json.model.Recipe
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModel
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModelFactory
import de.micmun.android.nextcloudcookbook.ui.MainActivity
import de.micmun.android.nextcloudcookbook.util.StorageManager
import de.micmun.android.nextcloudcookbook.util.json.RecipeJsonParser
import de.micmun.android.nextcloudcookbook.util.json.RecipeJsonWriter
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.net.URL

/**
 * Fragment for recipe download form.
 *
 * @author Leafar
 * @version 1.0, 18.04.21
 */
class DownloadFormFragment : Fragment(), DownloadClickListener {
   private lateinit var binding: FragmentDownloadFormBinding
   private lateinit var settingViewModel: CurrentSettingViewModel

   private var fragmentJob = Job()
   private val uiScope = CoroutineScope(Dispatchers.Main + fragmentJob)

   private var recipeDir: String? = null
   private val isDownloading = MutableLiveData(false)

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_download_form, container, false)
      binding.clickListener = this

      val factory = CurrentSettingViewModelFactory(MainActivity.mainApplication)
      settingViewModel = ViewModelProvider(MainActivity.mainApplication, factory)
              .get(CurrentSettingViewModel::class.java)

      settingViewModel.recipeDirectory.observe(viewLifecycleOwner, {
         recipeDir = it
      })

      isDownloading.observe (viewLifecycleOwner, { isDownloading ->
         binding.downloadBtn.isEnabled = !isDownloading
      })

      return binding.root
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      (requireActivity() as AppCompatActivity).supportActionBar
              ?.title = resources.getString(R.string.form_download_title)
   }

   override fun doDownload() {
      // TODO should we enable cleartext traffic (http)?
      val url = URL(binding.recipeUrlTxt.text.toString())
      val request = DownloadRequest(url, binding.recipeOverridePath.text.toString(),
              binding.replaceExistingChkBox.isChecked)

      isDownloading.postValue(true)
      uiScope.launch {
         downloadImpl(request.url)?.let { recipe ->
            if (recipe.name.isNotEmpty()) {
               val storage = StorageManager.getDocumentFromString(requireContext(), this@DownloadFormFragment.recipeDir ?: "")
               if (storage?.exists() == true) {
                  var recipeDir = storage.findFolder(recipe.name)
                  if (recipeDir == null || request.replaceExisting) {
                     if (recipeDir == null)
                        recipeDir = storage.createDirectory(recipe.name)
                     val recipeFile = recipeDir?.findFile("recipe.json")
                             ?:recipeDir?.createFile("application/json", "recipe.json")
                     val writer = recipeFile?.openOutputStream(requireContext(), false)?.bufferedWriter()
                     writer?.write(RecipeJsonWriter().write(recipe))
                     writer?.close()
                 }
               }
            }
         }
         isDownloading.postValue(false)
      }
   }

   private suspend fun downloadImpl(url: URL): Recipe? {
      //return Recipe(0, name = "Test")
      return withContext(Dispatchers.IO) {
         val document = Jsoup.connect(url.toString()).get()
         for (element in document.getElementsByTag("script")) {
            if (element.attr("type")?.equals("application/ld+json") == true) {
               val json = element.html()
               try {
                  val recipe = RecipeJsonParser().parse(json)
                  if (recipe != null && recipe.type == "Recipe") {
                     return@withContext recipe
                  }
               } catch (e: KlaxonException) {
                  Log.e("JSON", "Parsing error", e)
               }
            }
         }
         null
      }
   }

   private data class DownloadRequest(
           var url: URL,
           var overridePath: String?,
           var replaceExisting: Boolean
   )
}

interface DownloadClickListener {
   fun doDownload()
}
