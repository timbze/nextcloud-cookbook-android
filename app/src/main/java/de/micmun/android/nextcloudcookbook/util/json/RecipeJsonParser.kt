/*
 * RecipeJsonParser.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util.json

import com.beust.klaxon.*
import de.micmun.android.nextcloudcookbook.json.*
import de.micmun.android.nextcloudcookbook.json.model.Recipe
import java.io.StringReader
import java.lang.ClassCastException

/**
 * Parser for recipe json.
 *
 * @author MicMun
 * @version 1.1, 11.04.21
 */
class RecipeJsonParser {
   fun parse(json: String): Recipe? {
      return Klaxon()
         .fieldConverter(RecipeDate::class, DateConverter())
         .fieldConverter(RecipeStringList::class, ListConverter())
         .fieldConverter(RecipeListString::class, List2StringConverter())
         .fieldConverter(RecipeNutrition::class, NutritionConverter())
         .fieldConverter(Recipe2String::class, Value2StringConverter())
         .fieldConverter(RecipeAuthor::class, AuthorConverter())
         .parse<Recipe>(json)
   }

   fun parseFromWeb(json: String): Recipe? {
      val recipeParser = Klaxon()
              .fieldConverter(RecipeDate::class, DateConverter())
              .fieldConverter(RecipeStringList::class, ListConverter())
              .fieldConverter(RecipeListString::class, List2StringConverter())
              .fieldConverter(RecipeNutrition::class, NutritionConverter())
              .fieldConverter(Recipe2String::class, Value2StringConverter())
              .fieldConverter(RecipeAuthor::class, AuthorConverter())

      // websites may provide multiple ld-json in one script tag as an array
      try {
         val jsArray = Klaxon().parseJsonArray(StringReader(json))
         for (obj in jsArray) {
            // could also check js["@context"] == "http://schema.org"
            if (obj is JsonObject && obj["@type"] == "Recipe") {
               return recipeParser.parseFromJsonObject<Recipe>(obj)
            }
         }
      } catch (e : ClassCastException) {}

      val jsonObject = Klaxon().parseJsonObject(StringReader(json))
      if (jsonObject["@type"] == "Recipe") {
         return recipeParser.parseFromJsonObject<Recipe>(jsonObject)
      }
      return null
   }

}
