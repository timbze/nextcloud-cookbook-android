/*
 * StringArrayTypeAdapter.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * TypeAdapter for json string arrays.
 *
 * @author MicMun
 * @version 1.0, 18.03.20
 */
class StringArrayTypeAdapter : TypeAdapter<Array<String>>() {
   override fun write(out: JsonWriter?, value: Array<String>?) {
      TODO("Not yet implemented")
   }

   @Throws(IOException::class)
   override fun read(reader: JsonReader): Array<String>? {
      var array: Array<String> = emptyArray()

      try {
         when (reader.peek()) {
            JsonToken.NULL -> {
               reader.nextNull()
            }
            JsonToken.STRING -> {
               array = listOf(reader.nextString()).toTypedArray()
            }
            JsonToken.BEGIN_ARRAY -> {
               array = readArray(reader)
            }
            else -> array = emptyArray()
         }
      } catch (e: IOException) {
         e.printStackTrace()
      }

      if (array.size == 1 && array[0].contains("\r\n")) {
         val str = array[0].trim()
         array = str.split("\r\n").filter { s -> s.trim().isNotEmpty() }.toTypedArray()
      }

      return array
   }

   @Throws(IOException::class)
   private fun readArray(reader: JsonReader): Array<String> {
      val array: MutableList<String> = mutableListOf()

      reader.beginArray()
      while (reader.hasNext()) {
         val entry = reader.nextString()
         array.add(entry)
      }
      reader.endArray()
      return array.toTypedArray()
   }
}