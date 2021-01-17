/*
 * RecipeRepository.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileType
import com.anggrayudi.storage.file.absolutePath
import com.anggrayudi.storage.file.findFiles
import com.anggrayudi.storage.file.openInputStream
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.util.JsonRecipeParser
import de.micmun.android.nextcloudcookbook.util.StorageManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

/**
 * Repository with the recipe data.
 *
 * @author MicMun
 * @version 1.6, 27.06.20
 */
class RecipeRepository {
   private val _recipeList = mutableListOf<Recipe>()
   val recipeList: List<Recipe>
      get() = _recipeList
   private val _recipeMap = mutableMapOf<Long, Recipe>()
   private val _recipeCategories = mutableSetOf<String>()
   val recipeCategories: Set<String>
      get() = _recipeCategories

   companion object {
      @Volatile
      private var INSTANCE: RecipeRepository? = null

      fun getInstance(): RecipeRepository {
         synchronized(this) {
            var instance = INSTANCE

            if (instance == null) {
               instance = RecipeRepository()
               INSTANCE = instance
            }

            return instance
         }
      }
   }

   fun getRecipeWithId(id: Long) = _recipeMap[id]

   fun getCategoryTitle(id: Int): String {
      return _recipeCategories.first { c -> c.hashCode() == id }
   }

   fun filterRecipesWithCategory(id: Int): List<Recipe> {
      return _recipeList.filter { recipe ->
         if (recipe.recipeCategory == null) {
            false
         } else {
            try {
               recipe.recipeCategory?.first { c -> c.hashCode() == id }
               true
            } catch (e: NoSuchElementException) {
               false
            }
         }
      }
   }

   fun filterRecipesUncategorized(): List<Recipe> {
      return _recipeList.filter { recipe ->
         recipe.recipeCategory.isNullOrEmpty()
      }
   }

   /**
    * Reads all recipes from directory.
    */
   fun getAllRecipes(context: Context, path: String): List<Recipe> {
      val recipeDir = StorageManager.getDocumentFromString(context, path) ?: return emptyList()

      Log.d("RecipeRepository", "recipeDir = ${recipeDir.absolutePath}")
      if (recipeDir.exists()) {
         val subDirs = recipeDir.listFiles()
         var id: Long = 1

         if (subDirs.isNotEmpty()) {
            _recipeList.clear()
         }

         val tmpCategories = mutableSetOf<String>()
         val tmpRecipeMap = mutableMapOf<Long, Recipe>()

         subDirs.forEach { sd ->
            if (sd.exists() && sd.isDirectory) {
               val jsonFiles = sd.listFiles().filter { f -> f.name?.endsWith(".json") ?: false }
               val thumbFiles = sd.findFiles(arrayOf("thumb.jpg"), DocumentFileType.FILE)
               val fullFiles = sd.findFiles(arrayOf("full.jpg"), DocumentFileType.FILE)
               val jsonFile = if (jsonFiles.isNotEmpty()) jsonFiles.first() else null

               val thumbFile = if (thumbFiles.isNotEmpty()) thumbFiles.first() else null
               val fullFile = if (fullFiles.isNotEmpty()) fullFiles.first() else null

               if (jsonFile != null && jsonFile.exists() && jsonFile.canRead()) {
                  val recipe = readRecipe(context, jsonFile)

                  if (recipe != null) {
                     if (recipe.recipeCategory == null)
                        recipe.recipeCategory = emptyArray()
                     recipe.thumbImage = thumbFile
                     recipe.imageUrl = fullFile?.absolutePath ?: ""

                     recipe.recipeId = id++
                     _recipeList.add(recipe)
                     tmpRecipeMap[recipe.recipeId] = recipe

                     val categories = recipe.recipeCategory
                     val cats = mutableListOf<String>()
                     categories?.forEach { c ->
                        if (c.trim().isNotEmpty()) {
                           tmpCategories.add(c.trim())
                           cats.add(c.trim())
                        }
                     }
                     recipe.recipeCategory = cats.toTypedArray()
                  }
               }
            }
         }
         if (tmpCategories.isNotEmpty())
            _recipeCategories.clear()
         _recipeCategories.addAll(tmpCategories.toSortedSet())
         if (tmpRecipeMap.isNotEmpty())
            _recipeMap.clear()
         _recipeMap.putAll(tmpRecipeMap)
      }

      return _recipeList
   }

   /**
    * Reads a recipe and parse it to the Recipe data model.
    *
    * @param file recipe file to read.
    * @return Recipe.
    */
   private fun readRecipe(context: Context, file: DocumentFile): Recipe? {
      val inputStream = file.openInputStream(context)
      val reader = BufferedReader(InputStreamReader(inputStream))

      val json: String

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         json = reader.lines().collect(Collectors.joining("\n"))
      } else {
         val strBuilder = StringBuilder()
         var line = reader.readLine()

         while (line != null) {
            strBuilder.append(line)
            line = reader.readLine()
         }

         json = strBuilder.toString()
      }

      return JsonRecipeParser(json).parse()
   }
}
