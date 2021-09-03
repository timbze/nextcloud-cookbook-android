/*
 * SearchFormFragment.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.downloadform

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scale
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.anggrayudi.storage.file.findFolder
import com.anggrayudi.storage.file.openOutputStream
import de.micmun.android.nextcloudcookbook.MainApplication
import de.micmun.android.nextcloudcookbook.R
import de.micmun.android.nextcloudcookbook.databinding.FragmentDownloadFormBinding
import de.micmun.android.nextcloudcookbook.json.model.Recipe
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModel
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModelFactory
import de.micmun.android.nextcloudcookbook.ui.MainActivity
import de.micmun.android.nextcloudcookbook.util.StorageManager
import de.micmun.android.nextcloudcookbook.util.json.RecipeJsonConverter
import kotlinx.coroutines.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

/**
 * Fragment for recipe download form.
 *
 * @author Leafar
 * @version 1.2, 29.08.21
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

      val factory = CurrentSettingViewModelFactory(MainApplication.AppContext)
      settingViewModel = ViewModelProvider(MainApplication.AppContext, factory)
         .get(CurrentSettingViewModel::class.java)

      settingViewModel.recipeDirectory.observe(viewLifecycleOwner, {
         recipeDir = it
      })

      isDownloading.observe(viewLifecycleOwner, { isDownloading ->
         binding.downloadBtn.isEnabled = !isDownloading
      })

      return binding.root
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      @Suppress("DEPRECATION")
      super.onActivityCreated(savedInstanceState)
      (requireActivity() as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.form_download_title)
   }

   override fun doDownload() {
      val url = binding.recipeUrlTxt.text.toString()
      val overridePath = binding.recipeOverridePath.text.toString()
      val replaceExisting = binding.replaceExistingChkBox.isChecked

      isDownloading.postValue(true)
      uiScope.launch {
         fetchAndParse(url)?.let { pair ->
            val recipe = pair.first
            if (recipe.name.isNotEmpty()) {
               val storage =
                  StorageManager.getDocumentFromString(requireContext(), this@DownloadFormFragment.recipeDir ?: "")
               if (storage?.exists() == true) {
                  val recipeDirName = if (overridePath.isEmpty()) recipe.name else overridePath
                  var recipeDir = storage.findFolder(recipeDirName)
                  if (recipeDir == null || replaceExisting) {
                     if (recipeDir == null)
                        recipeDir = storage.createDirectory(recipeDirName)
                     val recipeFile = recipeDir?.findOrCreateFile("application/json", "recipe.json")
                     val writer = recipeFile?.openOutputStream(requireContext(), false)?.bufferedWriter()
                     // we write the full json to also keep fields we do not process yet
                     writer?.write(pair.second.toString())
                     writer?.close()

                     if (recipe.image?.isNotBlank() == true) {
                        val bm = fetchImage(recipe.image!!)
                        if (bm != null) {
                           saveAsJpeg(bm, recipeDir, "full.jpg")

                           // crop image to a square
                           val minExtent = bm.width.coerceAtMost(bm.height)
                           val cutoff = Pair(bm.width - minExtent, bm.height - minExtent)
                           val croppedBm =
                              Bitmap.createBitmap(bm, cutoff.first / 2, cutoff.second / 2, minExtent, minExtent)

                           val thumbnail = croppedBm.scale(144, 144)
                           saveAsJpeg(thumbnail, recipeDir, "thumb.jpg")
                           val thumbnail16 = croppedBm.scale(16, 16)
                           saveAsJpeg(thumbnail16, recipeDir, "thumb16.jpg")
                        }
                     }
                  } else {
                     downloadError("Directory '${recipeDirName}' already exists")
                  }
               } else {
                  downloadError("No recipe directory found. Check the settings")
               }
            } else {
               downloadError("Parsed recipe has no name")
            }
         }
         isDownloading.postValue(false)
      }
   }

   private fun sanitizeURL(str: String): String {
      // TODO should we enable cleartext traffic (http)?
      var url: URL
      url = try {
         URL(str)
      } catch (e: MalformedURLException) {
         try {
            URL("https://$str")
         } catch (e: MalformedURLException) {
            return str
         }
      }
      if (url.protocol != "https")
         url = URL("https", url.host, url.file)
      return url.toString()
   }

   private suspend fun fetchAndParse(url: String): Pair<Recipe, JsonObject>? {
      return withContext(Dispatchers.IO) {
         val document: Document
         try {
            document = Jsoup.connect(sanitizeURL(url)).get()
         } catch (e: MalformedURLException) {
            downloadError("Malformed URL")
            return@withContext null
         } catch (e: HttpStatusException) {
            downloadError("Http Error ${e.statusCode}")
            return@withContext null
         } catch (e: Exception) {
            downloadError("Connection failed")
            return@withContext null
         }
         for (element in document.getElementsByTag("script")) {
            if (element.attr("type")?.equals("application/ld+json") == true) {
               val json = element.html()
               try {
                  RecipeJsonConverter.parseFromWeb(json)?.let { jsonObj ->
                     // we may need to patch the source url into the json data
                     if (!jsonObj.containsKey("url")) {
                        val map = jsonObj.toMutableMap()
                        map["url"] = JsonPrimitive(url)
                        val newObj = JsonObject(map)
                        RecipeJsonConverter.parse(newObj)?.let { recipe ->
                           return@withContext Pair(recipe, newObj)
                        }
                     }
                  }
               } catch (e: SerializationException) {
                  Log.e("JSON", "Parsing error", e)
               }
            }
         }
         downloadError("No parsable recipe found")
         null
      }
   }

   private suspend fun fetchImage(url: String): Bitmap? {
      return withContext(Dispatchers.IO) {
         try {
            val stream = URL(sanitizeURL(url)).openStream()
            val bm = BitmapFactory.decodeStream(stream)
            stream.close()
            return@withContext bm
         } catch (e: MalformedURLException) {
            downloadError("Image URL malformed")
         } catch (e: IOException) {
            downloadError("IOError loading image")
         }
         null
      }
   }

   private fun saveAsJpeg(bm: Bitmap, directory: DocumentFile?, fileName: String) {
      val file = directory?.findOrCreateFile("image/jpeg", fileName)
      if (file != null) {
         val stream = file.openOutputStream(requireContext(), false)
         if (stream != null) {
            bm.compress(Bitmap.CompressFormat.JPEG, 95, stream)
            stream.close()
            return
         }
      }
      downloadError("Failed to save image $fileName")
   }

   private fun downloadError(message: String) {
      activity?.runOnUiThread {
         Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
      }
   }
}

fun DocumentFile.findOrCreateFile(mime: String, fileName: String): DocumentFile? {
   return findFile(fileName) ?: createFile(mime, fileName)
}

interface DownloadClickListener {
   fun doDownload()
}
