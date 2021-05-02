/*
 * RecipeJsonWriter.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util.json

import com.beust.klaxon.Klaxon
import de.micmun.android.nextcloudcookbook.json.*
import de.micmun.android.nextcloudcookbook.json.model.Recipe

/**
 * Writer for recipe json.
 *
 * @author MicMun
 * @version 1.0, 14.02.21
 */
class RecipeJsonWriter {
   fun write(recipe: Recipe): String {
      return Klaxon()
         .fieldConverter(RecipeDate::class, DateConverter())
         .fieldConverter(RecipeStringList::class, ListConverter())
         .fieldConverter(RecipeListString::class, List2StringConverter())
         .fieldConverter(RecipeNutrition::class, NutritionConverter())
         .fieldConverter(Recipe2String::class, Value2StringConverter())
         .fieldConverter(RecipeAuthor::class, AuthorConverter())
         .fieldConverter(RecipeImage::class, ImageURLConverter())
         .toJsonString(recipe)
   }
}
