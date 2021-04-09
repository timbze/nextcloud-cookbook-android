/*
 * NutritionConverter.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json

import com.beust.klaxon.*
import de.micmun.android.nextcloudcookbook.json.model.Nutrition

/**
 * Converter for Nutrition.
 *
 * @author MicMun
 * @version 1.0, 14.02.21
 */
class NutritionConverter : Converter {
   override fun canConvert(cls: Class<*>): Boolean {
      if (cls == JsonArray::class.java || cls == JsonObject::class.java) {
         return true
      }
      return false
   }

   override fun fromJson(jv: JsonValue): Any? {
      val obj: JsonObject = if (jv.type == JsonArray::class.java) {
         val array = jv.array ?: JsonArray<JsonObject>()
         if (array.size == 0)
            return Nutrition()
         else {
            val first = array[0] as JsonObject
            first
         }
      } else {
         jv.obj ?: JsonObject()
      }
      return Klaxon().parseFromJsonObject<Nutrition>(obj)
   }

   override fun toJson(value: Any): String {
      return Klaxon().toJsonString(value)
   }
}

@Target(AnnotationTarget.FIELD)
annotation class RecipeNutrition
