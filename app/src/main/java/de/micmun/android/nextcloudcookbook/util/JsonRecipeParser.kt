/*
 * JsonRecipeParser.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import com.google.gson.GsonBuilder
import de.micmun.android.nextcloudcookbook.data.model.Author
import de.micmun.android.nextcloudcookbook.data.AuthorTypeAdapter
import de.micmun.android.nextcloudcookbook.data.model.Recipe
import de.micmun.android.nextcloudcookbook.data.StringArrayTypeAdapter

/**
 * Parser for JSON in schema.org Recipe.
 *
 * @author MicMun
 * @version 1.0, 18.03.20
 */
class JsonRecipeParser(private val jsonStr: String) {
   /**
    * Returns the parsed recipe.
    *
    * @return Recipe.
    */
   fun parse(): Recipe {
      val gson = GsonBuilder()
         .registerTypeAdapter(Author::class.java, AuthorTypeAdapter())
         .registerTypeAdapter(Array<String>::class.java, StringArrayTypeAdapter())
         .create()
      return gson.fromJson(jsonStr, Recipe::class.java)
   }
}