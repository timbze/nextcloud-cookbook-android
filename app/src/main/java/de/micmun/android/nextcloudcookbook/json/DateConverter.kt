/*
 * DateConverter.kt
 *
 * Copyright 2021 by MicMun
 */
package de.micmun.android.nextcloudcookbook.json

import android.util.Log
import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeParseException
import java.util.*

/**
 * Converter for date object.
 *
 * @author MicMun
 * @version 1.0, 14.02.21
 */
class DateConverter : Converter {
   private val sdf = SimpleDateFormat.getDateInstance()

   override fun canConvert(cls: Class<*>): Boolean {
      if (cls == String::class.java)
         return true
      return false
   }

   override fun fromJson(jv: JsonValue): Any? {
      var date = jv.string ?: return null
      val pointPos = date.indexOf(".")
      if (pointPos != -1)
         date = date.substring(0, pointPos)
      val plusPos = date.indexOf("+")
      if (plusPos != -1)
         date = date.substring(0, plusPos)
      val zPos = date.indexOf("Z")
      if (zPos != -1)
         date = date.substring(0, zPos)

      date = date.replace(' ', 'T')

      val localDateTime = parseIso8861(date)
      if (localDateTime != null) {
         val ldate = Date(localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli())
         return sdf.format(ldate)
      }

      try {
         val time = date.toLong()
         val ldt = Date(time)
         return sdf.format(ldt)
      } catch (e1: NumberFormatException) {
         Log.e("DateConverter", "NumberFormatException: ${e1.localizedMessage}")
      } catch (e2: DateTimeException) {
         Log.e("DateConverter", "DateTimeException: ${e2.localizedMessage}")
      }

      return jv.string
   }

   override fun toJson(value: Any): String {
      val date = sdf.parse(value as String)
      return if (date != null) toJson(date) else ""
   }

   private fun parseIso8861(dateString: String): LocalDateTime? {
      return try {
         val date = LocalDate.parse(dateString)
         LocalDateTime.of(date, LocalTime.MIN)
      } catch (e: DateTimeParseException) {
         try {
            LocalDateTime.parse(dateString)
         } catch (e1: DateTimeParseException) {
            null
         }
      }
   }

   private fun toJson(date: Date): String {
      val ldt = LocalDateTime.ofEpochSecond(date.time / 1000, 0, ZoneOffset.UTC)
      return ldt.toString()
   }
}

@Target(AnnotationTarget.FIELD)
annotation class RecipeDate
