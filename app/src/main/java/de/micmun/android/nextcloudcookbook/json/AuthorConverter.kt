/*
 * AuthorConverter
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonValue
import de.micmun.android.nextcloudcookbook.json.model.Author

/**
 * Converter for author.
 *
 * @author MicMun
 * @version 1.0, 19.02.21
 */
class AuthorConverter : Converter {
   override fun canConvert(cls: Class<*>): Boolean {
      if (cls == Author::class.java || cls == String::class.java)
         return true
      return false
   }

   override fun fromJson(jv: JsonValue): Any {
      val obj = jv.obj ?: if (jv.array?.size ?: 0 > 0) jv.array?.get(0) as JsonObject else null
      return if (obj != null) {
         Author(obj["@type"].toString(), obj["name"].toString())
      } else {
         Author(name = jv.string)
      }
   }

   override fun toJson(value: Any): String {
      return if (value::class == Author::class) {
         val author = value as Author
         "{\"@type\" : \"${author.type ?: ""}\", \"name\": \"${author.name ?: ""}\"}"
      } else {
         val name = value as String
         "{\"@type\" : \"Person\", \"name\": \"$name\"}"
      }
   }
}

@Target(AnnotationTarget.FIELD)
annotation class RecipeAuthor
