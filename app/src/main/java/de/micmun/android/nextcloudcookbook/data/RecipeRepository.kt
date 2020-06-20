/*
 * RecipeRepository.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.net.toFile
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.util.JsonRecipeParser
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.stream.Collectors

/**
 * Repository with the recipe data.
 *
 * @author MicMun
 * @version 1.3, 20.06.20
 */
class RecipeRepository {
   private val _recipeList = mutableListOf<Recipe>()
   private val _recipeMap = mutableMapOf<Long, Recipe>()
   private val _recipeCategories = mutableSetOf<String>()

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

   fun getCategories(): Set<String> = _recipeCategories

   /**
    * Reads all recipes from directory.
    */
   fun getAllRecipes(path: String): List<Recipe> {
      val dir = File(path)

      if (dir.exists()) {
         val subdirs = dir.listFiles()
         var id: Long = 1

         if (subdirs != null && subdirs.isNotEmpty()) {
            _recipeCategories.clear()
            _recipeMap.clear()
            _recipeList.clear()
         }

         subdirs?.forEach { sd ->
            Log.d("RecipeDirectory", "sd = ${sd.absolutePath}")
            if (sd.exists() && sd.isDirectory) {
               val jsonFiles = sd.listFiles()?.filter { f -> f.name.endsWith(".json") }
               var jsonFile: File? = null

               if (jsonFiles != null && jsonFiles.isNotEmpty()) {
                  jsonFile = jsonFiles[0]
               }

               val thumbFile = File(sd, "thumb.jpg")
               val fullFile = File(sd, "full.jpg")

               if (jsonFile != null && jsonFile.exists()) {
                  val recipe = readRecipe(Uri.fromFile(jsonFile))
                  Log.d("RecipeDirectory", "recipe.name = ${recipe.name}")
                  if (recipe.recipeCategory == null)
                     recipe.recipeCategory = emptyArray()

                  recipe.thumbImage = if (thumbFile.exists()) Uri.fromFile(thumbFile) else null
                  recipe.imageUrl = if (fullFile.exists()) Uri.fromFile(fullFile).toString() else ""
                  recipe.recipeId = id++
                  _recipeList.add(recipe)
                  _recipeMap[recipe.recipeId] = recipe

                  val categories = recipe.recipeCategory
                  categories?.forEach { c -> _recipeCategories.add(c) }
               }
            }
         }
      }
      return _recipeList
   }

   /**
    * Reads a recipe and parse it to the Recipe data model.
    *
    * @param path Path of the file.
    * @return Recipe.
    */
   private fun readRecipe(path: Uri): Recipe {
      val reader = BufferedReader(FileReader(path.toFile()))

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
