/*
 * Value2StringConverter
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue

/**
 * Converter four double value.
 *
 * @author MicMun
 * @version 1.0, 19.02.21
 */
class Value2StringConverter : Converter {
   override fun canConvert(cls: Class<*>): Boolean {
      if (cls == Double::class.java || cls == String::class.java || cls == Int::class.java ||
          cls == Boolean::class.java
      )
         return true
      return false
   }

   override fun fromJson(jv: JsonValue): Any? {
      return if (jv.double != null)
         jv.double.toString()
      else if (jv.int != null)
         jv.int.toString()
      else if (jv.boolean != null)
         jv.boolean.toString()
      else {
         jv.string
      }
   }

   override fun toJson(value: Any): String {
      return value.toString()
   }
}

@Target(AnnotationTarget.FIELD)
annotation class Recipe2String
