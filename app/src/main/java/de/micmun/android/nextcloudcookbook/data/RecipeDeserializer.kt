/*
 * RecipeDeserializer.kt
 *
 * Copyright 2020 by MicMun
 */
package de.micmun.android.nextcloudcookbook.data

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.util.ISO8601Utils
import java.text.ParsePosition
import java.util.*

/**
 * An own type adapter for recipe dates (eg. datePublished).
 *
 * @author MicMun
 * @version 1.0, 02.07.20
 */
class RecipeDateDeserializer(d: Class<Date>?) : StdDeserializer<Date?>(d) {
   @Suppress("unused")
   internal constructor() : this(null) {
   }

   override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Date? {
      val value = p?.readValueAs(String::class.java)
      if (value != null) {
         var pos = value.indexOf(" ")
         if (pos == -1)
            pos = value.indexOf("T")

         return if (pos == -1)
            ISO8601Utils.parse(value, ParsePosition(0))
         else
            ISO8601Utils.parse(value.substring(0, pos), ParsePosition(0))
      }
      return value
   }
}

/**
 * An own type adapter for recipe arrays (eg. Instructions and Ingredients).
 *
 * @author MicMun
 * @version 1.0, 04.07.20
 */
class RecipeStringArrayDeserializer(a: Class<Array<String>>?) : StdDeserializer<Array<String>?>(a) {
   @Suppress("unused")
   internal constructor() : this(null)

   override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Array<String> {
      val value = p?.readValueAs(Array<String>::class.java)
      val array = mutableListOf<String>()

      value?.forEach { v ->
         if (v.contains("\r\n")) {
            val tmp = v.split("\r\n")
            tmp.forEach { t ->
               if (t.trim().isNotEmpty())
                  array.add(t.trim())
            }
         } else {
            array.add(v.trim())
         }
      }

      return array.toTypedArray()
   }
}
