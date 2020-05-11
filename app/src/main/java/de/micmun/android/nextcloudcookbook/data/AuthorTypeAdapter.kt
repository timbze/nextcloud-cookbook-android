/*
 * AuthorTypeAdapter
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import de.micmun.android.nextcloudcookbook.data.model.Author
import java.io.IOException

/**
 * TypeAdapter for type author.
 *
 * @author MicMun
 * @version 1.0, 18.03.20
 */
class AuthorTypeAdapter : TypeAdapter<Author>() {
   override fun write(out: JsonWriter?, value: Author?) {
      TODO("Not yet implemented")
   }

   @Throws(IOException::class)
   override fun read(reader: JsonReader): Author? {
      var name = ""

      try {
         when (reader.peek()) {
            JsonToken.NULL -> {
               reader.nextNull()
            }
            JsonToken.STRING -> {
               name = reader.nextString()
            }
            JsonToken.BEGIN_OBJECT -> {
               name = readObject(reader)
            }
            else -> name = ""
         }
      } catch (e: IOException) {
         return null
      }

      return Author(name)
   }

   @Throws(IOException::class)
   private fun readObject(reader: JsonReader): String {
      var str = ""

      reader.beginObject()
      while (reader.hasNext()) {
         val key = reader.nextName()
         val value = reader.nextString()

         if (key == "name") {
            str = value
         } else if (key == "url") {
            str += " ($value)"
         }
      }
      reader.endObject()

      return str
   }
}