/*
 * JsonRecipeParser.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.util

import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.micmun.android.nextcloudcookbook.data.model.Recipe

/**
 * Parser for JSON in schema.org Recipe.
 *
 * @author MicMun
 * @version 1.2, 02.07.20
 */
class JsonRecipeParser(private val jsonStr: String) {
   /**
    * Returns the parsed recipe.
    *
    * @return Recipe.
    */
   fun parse(): Recipe? {
      val mapper = jacksonObjectMapper()
         .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
         .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
         .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)

      return try {
         mapper.readValue(jsonStr)
      } catch (e: JsonMappingException) {
         Log.e("JsonRecipeParser", "Exception: ${e.localizedMessage}")
         null
      }
   }
}
