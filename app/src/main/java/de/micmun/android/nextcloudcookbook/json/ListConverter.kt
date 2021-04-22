/*
 * ListConverter.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json

import android.util.Log
import com.beust.klaxon.Converter
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonValue
import java.util.stream.Collectors

/**
 * Converter for list.
 *
 * @author MicMun
 * @version 1.0, 14.02.21
 */
class ListConverter : Converter {
   override fun canConvert(cls: Class<*>): Boolean {
      if (cls == JsonArray::class.java || cls == String::class.java || cls == JsonObject::class.java) {
         return true
      }
      return false
   }

   override fun fromJson(jv: JsonValue): Any {
      val array = jv.array ?: jv.string ?: ""

      if (array is JsonArray<*>) {
         return array.toList().stream().map { it.toString() }.collect(Collectors.toList()).toList()
      } else if (array is String && array.isNotEmpty()) {
         return arrayListOf(array)
      } else if (array is String && array.isEmpty()) {
         return emptyList<String>()
      } else {
         Log.e("ListConverter", "FEHLER: Unbekannter Typ {$jv")
      }

      return emptyList<String>()
   }

   override fun toJson(value: Any): String {
      val list = value as List<*>
      return JsonArray(list).toJsonString(prettyPrint = false, canonical = true)
   }
}

@Target(AnnotationTarget.FIELD)
annotation class RecipeStringList
