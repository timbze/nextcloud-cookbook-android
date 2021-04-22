/*
 * RecipeViewModel.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.ui.recipelist

import android.app.Application
import androidx.lifecycle.*
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.DocumentFileType
import com.anggrayudi.storage.file.openOutputStream
import com.beust.klaxon.KlaxonException
import de.micmun.android.nextcloudcookbook.data.CategoryFilter
import de.micmun.android.nextcloudcookbook.data.SortValue
import de.micmun.android.nextcloudcookbook.db.DbRecipeRepository
import de.micmun.android.nextcloudcookbook.db.model.DbRecipe
import de.micmun.android.nextcloudcookbook.json.JsonRecipeRepository
import de.micmun.android.nextcloudcookbook.json.model.Recipe
import de.micmun.android.nextcloudcookbook.ui.CurrentSettingViewModel
import de.micmun.android.nextcloudcookbook.util.Recipe2DbRecipeConverter
import de.micmun.android.nextcloudcookbook.util.StorageManager
import de.micmun.android.nextcloudcookbook.util.json.RecipeJsonParser
import de.micmun.android.nextcloudcookbook.util.json.RecipeJsonWriter
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.net.URL
import java.util.*
import java.util.stream.Collectors

/**
 * ViewModel for list of recipes.
 *
 * @author MicMun
 * @version 1.8, 26.01.21
 */
class RecipeListViewModel(private val app: Application) : AndroidViewModel(app) {
   // coroutines
   private var viewModelJob = Job()

   private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

   private val recipeRepository = DbRecipeRepository.getInstance(app)
   val categories = recipeRepository.getCategories()

   // on updating
   val isUpdating = MutableLiveData(false)
   val isLoaded = MutableLiveData(false)
   val isDownloading = MutableLiveData(false)

   private var recipeDir: String = ""

   // sorting and category
   private var sort: SortValue = SortValue.NAME_A_Z
   private var catFilter: CategoryFilter = CategoryFilter(CategoryFilter.CategoryFilterOption.ALL_CATEGORIES)

   // navigate to recipe
   private val _navigateToRecipe = MutableLiveData<String>()
   val navigateToRecipe
      get() = _navigateToRecipe

   fun onRecipeClicked(name: String) {
      _navigateToRecipe.value = name
   }

   fun onRecipeNavigated() {
      _navigateToRecipe.value = null
   }

   fun getRecipes(): LiveData<List<DbRecipe>> {
      var recipes: LiveData<List<DbRecipe>>

      runBlocking(Dispatchers.IO) {
         recipes =
            if (catFilter.type == CategoryFilter.CategoryFilterOption.ALL_CATEGORIES && sort == SortValue.NAME_A_Z) {
               recipeRepository.getAllRecipes()
            } else if (catFilter.type == CategoryFilter.CategoryFilterOption.ALL_CATEGORIES) {
               recipeRepository.sort(sort)
            } else if (catFilter.type == CategoryFilter.CategoryFilterOption.UNCATEGORIZED) {
               recipeRepository.filterUncategorized(sort)
            } else {
               recipeRepository.filterCategory(sort, catFilter.name)
            }
      }
      return recipes
   }

   // read recipes
   fun initRecipes(path: String = "") {
      if (path.isNotEmpty()) {
         recipeDir = path
      }
      val dir = if (path.isEmpty()) recipeDir else path
      if (dir.isEmpty())
         return

      isUpdating.postValue(true)

      uiScope.launch {
         val list = getRecipesFromRepo(dir)
         val dbList = list.stream()
            .map { Recipe2DbRecipeConverter(it).convert() }
            .collect(Collectors.toList())
         recipeRepository.insertAll(dbList)

         isLoaded.postValue(true)
         isUpdating.postValue(false)
      }
   }

   fun download(request: DownloadRequest) {
      isDownloading.postValue(true)
      uiScope.launch {
         downloadImpl(request.url)?.let { recipe ->
            CurrentSettingViewModel(app).recipeDirectory.value?.let { basedir ->
               val recipePath = "$basedir/${recipe.name}"
               if (recipePath.isNotEmpty()) {
                  val recipeDir = StorageManager.getDocumentFromString(app, recipePath)
                  if (recipeDir?.exists() == false || request.replaceExisting) {
                     DocumentFileCompat.mkdirs(app, recipePath)

                     DocumentFileCompat.fromFullPath(app, "$recipePath/recipe.json", DocumentFileType.FILE)?.let { jsonFile->
                        jsonFile.openOutputStream(app, false)
                                ?.bufferedWriter()
                                ?.write(RecipeJsonWriter().write(recipe))
                     }
                  }
               }
            }
         }
         isDownloading.postValue(false)
      }
   }

   private suspend fun downloadImpl(url: URL): Recipe? {
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
                  print(e)
               }
            }
         }
         null
      }
   }

   private suspend fun getRecipesFromRepo(path: String): List<Recipe> {
      return withContext(Dispatchers.IO) {
         JsonRecipeRepository.getInstance().getAllRecipes(app, path)
      }
   }

   // category filter
   fun filterRecipesByCategory(catFilter: CategoryFilter) {
      this.catFilter = catFilter
   }

   fun sortList(sort: SortValue) {
      this.sort = sort
   }

   override fun onCleared() {
      super.onCleared()
      viewModelJob.cancel()
   }
}

class RecipeListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
   @Suppress("UNCHECKED_CAST")
   override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(RecipeListViewModel::class.java)) {
         return RecipeListViewModel(application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
   }
}

data class DownloadRequest(
   var url: URL,
   var overridePath: String?,
   var replaceExisting: Boolean
)