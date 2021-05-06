/*
 * AuthorConverter
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue

/**
 * Converter for the recipe's image URL.
 *
 * @author Leafar
 * @version 1.0, 02.05.21
 */
class ImageURLConverter : Converter {
   override fun canConvert(cls: Class<*>): Boolean {
      return cls == String::class.java
   }

   override fun fromJson(jv: JsonValue): Any? {
      if (jv.obj?.containsKey("url") == true)
         return jv.obj!!["url"]
      return jv.string
   }

   override fun toJson(value: Any): String {
      return "\"$value\""
   }
}

@Target(AnnotationTarget.FIELD)
annotation class RecipeImage
