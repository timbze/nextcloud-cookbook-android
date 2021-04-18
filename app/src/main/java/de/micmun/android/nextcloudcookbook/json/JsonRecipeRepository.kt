/*
 * JsonRecipeRepository.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json

import android.content.Context
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileType
import com.anggrayudi.storage.file.findFiles
import com.anggrayudi.storage.file.openInputStream
import de.micmun.android.nextcloudcookbook.json.model.Recipe
import de.micmun.android.nextcloudcookbook.util.StorageManager
import de.micmun.android.nextcloudcookbook.util.json.RecipeJsonParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

/**
 * Repository with the recipe data.
 *
 * @author MicMun
 * @version 1.9, 17.04.21
 */
class JsonRecipeRepository {
   companion object {
      @Volatile
      private var INSTANCE: JsonRecipeRepository? = null

      fun getInstance(): JsonRecipeRepository {
         synchronized(this) {
            var instance = INSTANCE

            if (instance == null) {
               instance = JsonRecipeRepository()
               INSTANCE = instance
            }

            return instance
         }
      }
   }

   /**
    * Reads all recipes from directory.
    */
   fun getAllRecipes(context: Context, path: String): List<Recipe> {
      val recipeDir = StorageManager.getDocumentFromString(context, path) ?: return emptyList()
      val recipeList = mutableListOf<Recipe>()

      if (recipeDir.exists()) {
         val subDirs = recipeDir.listFiles()

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
                     recipe.thumbImageUrl = thumbFile?.uri?.toString() ?: ""
                     recipe.fullImageUrl = fullFile?.uri?.toString() ?: ""

                     recipeList.add(recipe)

                     val categories = recipe.recipeCategory
                     val cats = mutableListOf<String>()
                     categories?.forEach { c ->
                        if (c.trim().isNotEmpty()) {
                           cats.add(c.trim())
                        }
                     }
                     recipe.recipeCategory = cats.toList()
                  }
               }
            }
         }
      }

      return recipeList
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

      return RecipeJsonParser().parse(json)
   }
}
