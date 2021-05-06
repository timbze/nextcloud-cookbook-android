/*
 * ListConverter.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json

import android.util.Log
import com.beust.klaxon.*
import java.util.stream.Collectors

/**
 * Converter that accepts a string or a list of strings from json.
 * A list will be joined into a comma-separated string.
 *
 * @author Leafar
 * @version 1.0, 22.04.21
 */
class List2StringConverter : Converter {
   override fun canConvert(cls: Class<*>): Boolean {
      return cls == JsonArray::class.java || cls == String::class.java
   }

   override fun fromJson(jv: JsonValue): Any {
      jv.array?.let {
         return it.toList().joinToString(",")
      }
      jv.string?.let {
         return it
      }
      Log.w("List2StringConverter", "Unknown type or missing field")
      return ""
   }

   override fun toJson(value: Any): String {
      return "\"${value as String}\""
   }
}

@Target(AnnotationTarget.FIELD)
annotation class RecipeListString
