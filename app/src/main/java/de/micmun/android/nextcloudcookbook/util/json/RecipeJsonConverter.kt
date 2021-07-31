/*
 * RecipeJsonConverter.kt
 *
 * Copyright 2021 by Leafar
 */
package de.micmun.android.nextcloudcookbook.util.json

import de.micmun.android.nextcloudcookbook.json.model.Recipe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import java.lang.Exception

/**
 * Convert between Recipe objects and their json representation.
 *
 * @author MicMun
 * @version 1.1, 11.07.21
 */
class RecipeJsonConverter {
   companion object {
      fun write(recipe: Recipe): String {
         return getParser().encodeToString(Recipe.serializer(), recipe)
      }

      fun parse(json: String): Recipe? {
         return try {
            getParser().decodeFromString(Recipe.serializer(), json)
         } catch (e: SerializationException) {
            null
         }
      }

      fun parse(json: JsonObject): Recipe? {
         return try {
            getParser().decodeFromJsonElement(Recipe.serializer(), json)
         } catch (e: SerializationException) {
            null
         }
      }

      fun parseFromWeb(json: String): JsonObject? {
         // websites may provide multiple ld-json in one script tag as an array
         try {
            val jsArray = getParser().parseToJsonElement(json).jsonArray

            for (obj in jsArray) {
               // could also check js["@context"] == "http://schema.org"
               if (obj is JsonObject && obj.jsonObject["@type"]?.jsonPrimitive?.content ?: "" == "Recipe") {
                  return obj
               }
            }
            return null
         } catch (e: Exception) {
         }

         val jsonObject = getParser().parseToJsonElement(json).jsonObject
         if (jsonObject["@type"]?.jsonPrimitive?.content ?: "" == "Recipe") {
            return jsonObject
         }
         return null
      }

      private fun getParser(): Json {
         return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
         }
      }
   }
}