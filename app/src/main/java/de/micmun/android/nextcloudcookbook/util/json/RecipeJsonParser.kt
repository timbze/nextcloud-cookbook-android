/*
 * RecipeJsonParser.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util.json

import com.beust.klaxon.Klaxon
import de.micmun.android.nextcloudcookbook.json.*
import de.micmun.android.nextcloudcookbook.json.model.Recipe

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
         .fieldConverter(RecipeNutrition::class, NutritionConverter())
         .fieldConverter(Recipe2String::class, Value2StringConverter())
         .fieldConverter(RecipeAuthor::class, AuthorConverter())
         .parse<Recipe>(json)
   }
}
